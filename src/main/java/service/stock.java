package service;

import bean.Account;
import sun.rmi.server.InactiveGroupException;
import util.JDBC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class stock {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws ParseException {








//        byMaxChangeEveryday(1000000, 200000000,"20200101","20210107");//1487644
//        byMaxChangeEveryday(1000000, 200000000,"20190101","20200107");//1694472
//        byMaxChangeEveryday(1000000, 200000000,"20180101","20190107");//726513
//        byMaxChangeEveryday(1000000, 200000000,"20170101","20180107");//1547660

        //history
//        固定100股
//        byMaxChangeEveryday(1000000, 100000000,"20200101","20210107");//1427016
//        byMaxChangeEveryday(1000000, 100000000,"20190101","20200107");//1630512
//        byMaxChangeEveryday(1000000, 100000000,"20180101","20190107");//749720
//        byMaxChangeEveryday(1000000, 100000000,"20170101","20180107");//1495459

    }

    public static void byMaxChangeEveryday(int initCash, int changeThreshold, String beginDate, String endDate) {

        List<Map<String, String>> t1 = JDBC.query("hkex", "select min(trade_no) as trade_no from trade_cal where date >='"+beginDate+"'" );
        int tradeDay= Integer.valueOf((String) t1.get(0).get("trade_no"));

        List<Map<String, String>> t2 = JDBC.query("hkex", "select max(trade_no) as trade_no from trade_cal where date <='"+endDate+"'" );
        int endDay=Integer.valueOf((String) t2.get(0).get("trade_no"));

        Account account = new Account(initCash);
        double baseCash = initCash;
        double remainingCash = initCash;

        while (true) {
            int buyTotal=0;
            int buySuccess=0;
            int sellTotal=0;
            int sellSucces=0;
            tradeDay++;
            if (tradeDay > endDay) {
                break;
            }
            List<Map<String, String>> hkex = JDBC.query("hkex", "select * from (\n" +
                    "select a.code,(a.shareholding-b.shareholding)*b.next_day_open as change,a.date,a.next_day_open,b.next_day_open as open \n" +
                    "from (select code,shareholding,date,next_day_open from hkex where trade_day_cnt='" + tradeDay + "') a\n" +
                    "left join\n" +
                    "    (select code,shareholding,date,next_day_open from hkex where trade_day_cnt='" + (tradeDay - 1) + "') b\n" +
                    "on a.code=b.code )t\n" +
                    "where ABS(change)>" + changeThreshold +" order by ABS(change) desc limit 10");
            for (Map<String, String> map : hkex ) {
                String code = map.get("code");
                String next_day_open1 = map.get("next_day_open");
                if (next_day_open1 == null) {
                    next_day_open1 = map.get("open");
                }
                double next_day_open =Double.valueOf (next_day_open1);
                double change = Double.valueOf( map.get("change"));
                if (change > 0) {
                    buyTotal++;
                    if(account.buyStock(code, change,next_day_open,changeThreshold)){
                        buySuccess++;
                    }
                }else {
                    sellTotal++;
                    if(account.buyStock(code, change,next_day_open,changeThreshold)){
                        sellSucces++;
                    }
                }

            }
            System.out.println(String.format("%s exchange service.stock:%s\tbuy:%s/%s\tsell:%s/%s\tcash:%s", tradeDay, hkex.size(),buySuccess,buyTotal,sellSucces,sellTotal,account.getCash()));

        }
        System.out.println(account.toString(--tradeDay));
    }

}
