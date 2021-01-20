package crawler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.HttpUtil;
import util.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class HKEX {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    static Gson gson = new Gson();
    static JsonParser jsonParser = new JsonParser();
    static int filter_days = 2;
    static int filter_days_hkex = 2;

    public static void main(String[] args) throws Exception {





//        List<Map> daily = JDBC.query("hkex", "select count(*) as aa from daily limit 100 ;");
//        long dailyCntBefore =Long.valueOf ((String)daily.get(0).get("aa"));
//        String dailyNowDate = getexesitMaxDate("daily");
//        updateDailyAll(dailyNowDate);
//        List<Map> daily2 = JDBC.query("hkex", "select count(*) as aa from daily limit 100 ;");
//        long dailyCntAfter =Long.valueOf ((String)daily2.get(0).get("aa"));
//        System.out.println("DAILY UPDATE:"+(dailyCntAfter-dailyCntBefore));
//
//
//
        List<Map> hkex = JDBC.query("hkex", "select count(*) as aa from hkex limit 100 ;");
        long hkexBefore =Long.valueOf ((String)hkex.get(0).get("aa"));
        String hkexNowDate = getexesitMaxDate("hkex");
        updateHkexAll(hkexNowDate);
        List<Map> hkex2 = JDBC.query("hkex", "select count(*) as aa from hkex limit 100 ;");
        long hkexAfter =Long.valueOf ((String)hkex2.get(0).get("aa"));
        System.out.println("HKXE UPDATE:"+(hkexAfter-hkexBefore));

//        trade_cal("20211231");
    }

    public static String getexesitMaxDate(String tableName) throws Exception {
        List<Map> hkex = JDBC.query("hkex", "select max(date) as maxval from " + tableName + " limit 10");
        String date = hkex.get(0).get("maxval").toString();
        return date;
    }


    public static void updateDailyAll(String hkexNowDate) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(hkexNowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (true) {
            cal.add(Calendar.DATE, 1);
            System.out.println(sdf.format(cal.getTime()));
            try {
                daily(sdf.format(cal.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("update daily stop by:" + sdf.format(cal.getTime()));
                break;
            }
        }
    }
    public static void updateHkexAll(String hkexNowDate) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(hkexNowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (true) {
            cal.add(Calendar.DATE, 1);
            System.out.println(sdf.format(cal.getTime()));
            try {
                updateHkexByDateV2(sdf.format(cal.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("update HkexAll stop by:" + sdf.format(cal.getTime()));
                break;
            }
        }
    }
    public static void updateHkexByDateV2(String dateStr) throws Exception {
        List<Map> trade_cal = JDBC.query("hkex", "select trade_no from trade_cal where date='" + dateStr + "'");
        if (trade_cal.isEmpty()) {
            return;
        }
        String trade_day_cnt = trade_cal.get(0).get("trade_no").toString();
        Thread.sleep(30000);
        String json = "{\n" +
                "    \"api_name\": \"hk_hold\",\n" +
                "    \"token\": \"a32a696dc94d7bce43aeeb6bcf9b3f17253e708c4b1e28d07e5d4974\",\n" +
                "    \"params\": {\n" +
                "        \"trade_date\":\""+dateStr+"\"\n" +
                "    },\n" +
                "    \"fields\": \"code,name,ts_code,trade_date,vol,exchange\"\n" +
                "}";
        String s = HttpUtil.httpPost("http://api.waditu.com", null, json);
        JsonArray jsonArray = jsonParser.parse(s).getAsJsonObject().get("data").getAsJsonObject().getAsJsonArray("items");
        Iterator<JsonElement> iterator = jsonArray.iterator();
        String insertSql = "";
        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            String s1 = next.toString().replace("[", "").replace("]", "").replace("\"", "");
            String[] split = s1.split(",");
            String code = split[2].split("\\.")[0];
            String name = split[3];
            String holding = split[4];
            String exchange = split[5];
            if (!exchange.equals("SH")&&!exchange.equals("SZ")) {
                continue;
            }
            if (filter_days_hkex > 0) {
                insertSql += "insert into hkex select '" + code + "','" + name + "','" + holding + "','" + dateStr + "','" + trade_day_cnt + "' where not exists (select * from hkex where code = '" + code+ "' and date ='" + dateStr + "'); \n";
            } else {
                insertSql += "insert into hkex select '" + code + "','" + name + "','" + holding + "','" + dateStr + "','" + trade_day_cnt +  "'; \n";
            }
        }
        System.out.println(insertSql);
        JDBC.update("hkex", insertSql);
        filter_days_hkex--;
    }



    public static void daily(String date) throws Exception {
        List<Map> trade_cal = JDBC.query("hkex", "select trade_no from trade_cal where date='" + date + "'");
        if (trade_cal.isEmpty()) {
            return;
        }
        String trade_day_cnt = trade_cal.get(0).get("trade_no").toString();
        String json = "{\n" +
                "    \"api_name\": \"daily\",\n" +
                "    \"token\": \"a32a696dc94d7bce43aeeb6bcf9b3f17253e708c4b1e28d07e5d4974\",\n" +
                "    \"params\": {\n" +
                "        \"trade_date\": \"" + date + "\"\n" +
                "    },\n" +
                "    \"fields\": \"ts_code,trade_date,open,high,low,close,pct_chg\"\n" +
                "}";
        String s = HttpUtil.httpPost("http://api.waditu.com", null, json);
        Thread.sleep(50);
        JsonArray jsonArray = jsonParser.parse(s).getAsJsonObject().get("data").getAsJsonObject().getAsJsonArray("items");
        Iterator<JsonElement> iterator = jsonArray.iterator();
        String insertSql = "";
        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            String s1 = next.toString().replace("[", "").replace("]", "").replace("\"", "");
            String[] split = s1.split(",");
            String code = split[0].split("\\.")[0];
//            String date=split[1];
            String open = split[2];
            String high = split[3];
            String low = split[4];
            String close = split[5];
            String pct_chg = split[6];
            if (filter_days > 0) {
                insertSql += "insert into daily select '" + code + "','" + open + "','" + high + "','" + low + "','" + close + "','" + pct_chg + "','" + date + "','" + trade_day_cnt + "' where not exists (select * from daily where code = '" + code + "' and date ='" + date + "'); \n";
            } else {
                insertSql += "insert into daily select '" + code + "','" + open + "','" + high + "','" + low + "','" + close + "','" + pct_chg + "','" + date + "','" + trade_day_cnt + "' ; \n";
            }
        }
        System.out.println(insertSql);
        JDBC.update("hkex", insertSql);
        filter_days--;
    }

    public static void trade_cal(String date) throws Exception {
        System.out.println("bengin trade_cal  " + date);
        String json = "{\n" +
                "    \"api_name\": \"trade_cal\",\n" +
                "    \"token\": \"a32a696dc94d7bce43aeeb6bcf9b3f17253e708c4b1e28d07e5d4974\",\n" +
                "    \"params\": {\n" +
                "        \"exchange\": \"SSE\",\n" +
                "        \"cal_date\":\"" + date + "\"\n" +
                "    },\n" +
                "    \"fields\": \"exchange,cal_date,is_open,pretrade_date\"\n" +
                "}";
        String s = HttpUtil.httpPost("http://api.waditu.com", null, json);
        Thread.sleep(1000);
        JsonArray jsonArray = jsonParser.parse(s).getAsJsonObject().get("data").getAsJsonObject().getAsJsonArray("items");
        Iterator<JsonElement> iterator = jsonArray.iterator();

        JsonElement next = iterator.next();
        String s1 = next.toString().replace("[", "").replace("]", "").replace("\"", "");
        String[] split = s1.split(",");

        String is_open = split[2];//交易日

        String pretrade_date = split[3];//前一个交易日
        if (Integer.valueOf(is_open) == 0) {
            trade_cal(pretrade_date);
            return;
        }
        List<Map> hkex = JDBC.query("hkex", "select * from trade_cal where date='" + pretrade_date + "'");
        if (hkex.isEmpty()) {
            trade_cal(pretrade_date);
            hkex = JDBC.query("hkex", "select * from trade_cal where date='" + pretrade_date + "'");
        }
        int trade_no = Integer.valueOf((String) hkex.get(0).get("trade_no"));
        trade_no++;
        String insertSql = "insert into trade_cal select '" + trade_no + "','" + date + "'  where not exists (select * from trade_cal where date ='" + date + "'); \n";
        System.out.println(insertSql);
        JDBC.update("hkex", insertSql);
    }
}
