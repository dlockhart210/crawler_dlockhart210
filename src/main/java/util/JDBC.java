package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBC {



    public static void main(String[] args) {
//      update("hkex","create table trade_cal(date date,is_open INT, pretrade_date date);");
//      update("hkex","create table daily(code varchar(20),open DOUBLE,high DOUBLE,low DOUBLE,close DOUBLE,pct_chg DOUBLE, date date);");

//        List<Map> hkex = query("hkex", "select max(date) as maxval from hkex limit 10");
//        System.out.println(hkex.get(0).get("maxval"));
//
//        List<Map> hkex1 = query("hkex", "select * from hkex where date = '2021/01/12' limit 10");
//        hkex1.forEach(x-> System.out.println(x.toString()));

//        update("hkex","delete from hkex where date = '2021/01/13'");
        System.out.println("done");
    }


    public static void update(String dbName, String sql){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbName+".db");
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            conn.close(); //结束数据库的连接
        }
        catch( Exception e )
        {
            e.printStackTrace ( );
        }
    }
    public static List<Map<String,String>> query(String dbName, String sql){
        List<Map<String,String>> list =new ArrayList<>();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbName+".db");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql); //查询数据
            while (rs.next()){
                String col="";
                ResultSetMetaData metaData = rs.getMetaData();
                Map map = new HashMap();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    col +=rs.getString(i)+"\t";
                    map.put(metaData.getColumnName(i),rs.getString(i));
                }
                list.add(map);
//                System.out.println(col);
            }
            rs.close();
            conn.close(); //结束数据库的连接
        }
        catch( Exception e )
        {
            e.printStackTrace ( );
        }
        return list;
    }
}
