package util;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            System.err.println("httpGet is miss!");
            return null;
        }
        return jp.parse(sb.toString()).getAsJsonObject();
    }

    public static String httpPost(String url, Map<String,String> attrMap,String json) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        if(json!=null){
            httppost.setEntity(new StringEntity(json));
        }else{
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            attrMap.forEach((k,v) -> params.add(new BasicNameValuePair(k, v)));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        }
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }


}
