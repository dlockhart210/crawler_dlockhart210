package crawler;


import com.google.gson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;

/**
 * @author dumengjie
 * @description
 * @create 2019-08-02
 */
public class HttpUtil {
    static Gson gson = new Gson();
    static JsonParser jp = new JsonParser();
public static String cookie=null;

    public static JsonObject httpGet(String urls) {
        URL url = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urls);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Cookie", cookie);
            conn.setDoInput(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            System.err.println("ans is miss!");
            return null;
        }
        return jp.parse(sb.toString()).getAsJsonObject();
    }


}
