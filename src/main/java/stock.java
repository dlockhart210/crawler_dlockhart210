import util.JDBC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class stock {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws ParseException {
//        byStockId(hk_code,dailu_code,20000000);
//        byStockId(hk_code,dailu_code,40000000);
//        byStockId(hk_code,dailu_code,40000000);

        String hk_code="90031";
        String dailu_code = "600031";
        byStockId(hk_code,dailu_code,10000);
        byStockId(hk_code,dailu_code,100000);
        byStockId(hk_code,dailu_code,5000000);
        byStockId(hk_code,dailu_code,10000000);
        byStockId(hk_code,dailu_code,20000000);
        byStockId(hk_code,dailu_code,40000000);

    }

    public static void byStockId(String hk_code,String dailu_code,int min_set) throws ParseException {

        int hold = 0;
        double cash = 1000000;
        double baseCash = cash;
        int trate_count=0;
        double unUseCash = cash;
        JDBC.query("hkex", "select * from daily where code='90900'");
        List<Map> hkex = JDBC.query("hkex", "select * from hkex where code='"+hk_code+"' order by trade_day_cnt");
        List<Map> daily = JDBC.query("hkex", "select * from daily where code='"+dailu_code+"' order by trade_day_cnt");
        Map<String, Double> dailyMap = new HashMap<>();
        for (int i = 0; i < daily.size(); i++) {
            double open = Double.valueOf((String) daily.get(i).get("open"));
            String date = (String) daily.get(i).get("date");
            dailyMap.put(date, open);
        }
        double lastPrice = 0;
        for (int i = 1; i < hkex.size() - 1; i++) {
            long day1hold = Long.valueOf((String) hkex.get(i - 1).get("shareholding"));
            long day2hold = Long.valueOf((String) hkex.get(i).get("shareholding"));
            String date3 = (String) hkex.get(i + 1).get("date");
            double openPrice = dailyMap.get(date3);
            lastPrice = openPrice;
            long diff = day2hold - day1hold;
            if (Math.abs(diff) < min_set) { //
                continue;
            }
            if(diff==0){
                continue;
            }
            if (diff > 0) {
                if (100 * openPrice < cash) {
                    hold += 100;
                    cash -= 100 * openPrice;
                }
            } else  {
                if (hold >= 100) {
                    hold -= 100;
                    cash += 100 * openPrice;
                }
            }
            trate_count++;
            unUseCash = Math.min(unUseCash, cash);
//            System.out.println("date" + date3 + " cash:" + cash + " ,hold:" + hold + " " + " ,total:" + (cash + hold * lastPrice));
        }

        System.out.println("cash:" + cash + " ,hold:" + hold + " " + " ,float:" + (cash + (hold * lastPrice)) +" trate_count:"+trate_count
                +" ,useCash:" + (baseCash - unUseCash) + " P:" + (cash + (hold * lastPrice) - baseCash) / (baseCash - unUseCash)+" min_set:"+min_set);
        Double firstDayPrice = dailyMap.get(hkex.get(0).get("date"));
        Double lastDayPrice = dailyMap.get(hkex.get(hkex.size() - 1).get("date"));
        Double p = (lastDayPrice - firstDayPrice) / firstDayPrice;
        System.out.println("firstDayPrice:" + firstDayPrice + " lastDayPrice:" + lastDayPrice + " P:" + p);
        System.out.println("-----------------");
    }

    public static void byMaxChangeEveryday(String hk_code,String dailu_code,int min_set) throws ParseException {

        int hold = 0;
        double cash = 1000000;
        double baseCash = cash;
        int trate_count=0;
        double unUseCash = cash;
        JDBC.query("hkex", "select * from daily where code='90900'");
        List<Map> hkex = JDBC.query("hkex", "select * from hkex where code='"+hk_code+"' order by trade_day_cnt");
        List<Map> daily = JDBC.query("hkex", "select * from daily where code='"+dailu_code+"' order by trade_day_cnt");
        Map<String, Double> dailyMap = new HashMap<>();
        for (int i = 0; i < daily.size(); i++) {
            double open = Double.valueOf((String) daily.get(i).get("open"));
            String date = (String) daily.get(i).get("date");
            dailyMap.put(date, open);
        }
        double lastPrice = 0;
        for (int i = 1; i < hkex.size() - 1; i++) {
            long day1hold = Long.valueOf((String) hkex.get(i - 1).get("shareholding"));
            long day2hold = Long.valueOf((String) hkex.get(i).get("shareholding"));
            String date3 = (String) hkex.get(i + 1).get("date");
            double openPrice = dailyMap.get(date3);
            lastPrice = openPrice;
            long diff = day2hold - day1hold;
            if (Math.abs(diff) < min_set) { //
                continue;
            }
            if(diff==0){
                continue;
            }
            if (diff > 0) {
                if (100 * openPrice < cash) {
                    hold += 100;
                    cash -= 100 * openPrice;
                }
            } else  {
                if (hold >= 100) {
                    hold -= 100;
                    cash += 100 * openPrice;
                }
            }
            trate_count++;
            unUseCash = Math.min(unUseCash, cash);
//            System.out.println("date" + date3 + " cash:" + cash + " ,hold:" + hold + " " + " ,total:" + (cash + hold * lastPrice));
        }

        System.out.println("cash:" + cash + " ,hold:" + hold + " " + " ,float:" + (cash + (hold * lastPrice)) +" trate_count:"+trate_count
                +" ,useCash:" + (baseCash - unUseCash) + " P:" + (cash + (hold * lastPrice) - baseCash) / (baseCash - unUseCash)+" min_set:"+min_set);
        Double firstDayPrice = dailyMap.get(hkex.get(0).get("date"));
        Double lastDayPrice = dailyMap.get(hkex.get(hkex.size() - 1).get("date"));
        Double p = (lastDayPrice - firstDayPrice) / firstDayPrice;
        System.out.println("firstDayPrice:" + firstDayPrice + " lastDayPrice:" + lastDayPrice + " P:" + p);
        System.out.println("-----------------");
    }
}
