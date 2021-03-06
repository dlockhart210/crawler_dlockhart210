package crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.FileUtil;
import util.HttpUtil;

import java.util.*;

/**
 * @author dumengjie
 * @description
 * @create 2019-08-04
 */
public class ZH_V2 {
    static String filepath = "/Users/admin/Downloads/";
    static String ansPath = filepath + "zhihu_answers.txt";
    static String commitPath = filepath + "zhihu_commit.txt";
    static String ansCommitTimePath = filepath + "zh_ans_comm_time.txt";

    static long quest = 0;
    static long err = 0;
    static List<String> blackList = Arrays.asList("04607f4149ef669676ab27581da01855");
    static boolean isTarget = false;

    static long max = 1000;
    static long n = 0;
    static Map<String, String> map;
    static Map<String, String> map2;

    static long updates = 0;

    public static void main(String[] args) throws Exception {
        HttpUtil.cookie = "_zap=edbb8c61-7467-4063-a3c9-508b41862eb3; _xsrf=410e0b3a-9cbf-4502-8c51-9a0b5afe9b5f; d_c0=\"AOAgC8YO2A-PTv4IKB-vWpCcFoHLC3C5Ams=|1564977425\"; anc_cap_id=7d30761695384ce79098fa26ea3f020f; q_c1=04554eb4930543a587760a7162f5e43c|1577538685000|1567295029000; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1579492441,1579492809,1579572666,1579573429; tst=r; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1580797206; serverData=true; _cid=\"2|1:0|10:1580797218|4:_cid|28:MTIwODAzMTc3Mjg1NTEwNzU4NA==|c06c39caad18e7dac361054366e0c102651e4576ff2e60f2cb2c8325ba257204\"; KLBRSID=9d75f80756f65c61b0a50d80b4ca9b13|1580797225|1580797206";


        fun();
        System.out.println(new Date().toString());
        if (isTarget) {
            System.err.println("！！！！！！！GET IT！！！！！！！！！！！！");
        }

//        getUser("519253344", "juno");
    }


    public static void fun() throws Exception {
        map = FileUtil.getFirstAndI(ansCommitTimePath, 1);
        map2 = FileUtil.getFirstAndI(commitPath, 1);

        readit(null, map);
        FileUtil.saveMap(ansCommitTimePath, map);
        System.out.println("--------------------------------更新commit完成:" + n + ", updates:" + updates + ", quests:" + quest);


    }

    public static void readit(String s, Map<String, String> map) throws Exception {


        if (s == null) {
            s = "https://www.zhihu.com/api/v4/questions/275359100/answers?include=data%5B*%5D.is_normal%2Cadmin_closed_comment%2Creward_info%2Cis_collapsed%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cis_labeled%2Cis_recognized%2Cpaid_info%2Cpaid_info_content%3Bdata%5B*%5D.mark_infos%5B*%5D.url%3Bdata%5B*%5D.author.follower_count%2Cbadge%5B*%5D.topics&offset=0&limit=20&sort_by=updated";
        }
        JsonObject jo = HttpUtil.httpGet(s);
        if (jo == null) {
            System.out.println("list get err!");
            return;
        }
        String next = jo.get("paging").getAsJsonObject().get("next").getAsString();
        quest++;
        JsonArray anses = jo.get("data").getAsJsonArray();
        for (JsonElement ans : anses) {
            String ansId = ans.getAsJsonObject().get("id").getAsString();
            long updatetime = ans.getAsJsonObject().get("updated_time").getAsLong() * 1000;
            String updateTimestr = new Date(updatetime).toString();
            System.out.println(updateTimestr + "   " + ansId);
            if (n > max) {
                return;
            }
            n++;
            String urlstr = "https://www.zhihu.com/api/v4/answers/" + ansId + "/comments?limit=20&offset=0&order=reverse&status=open";
            if (map.get(ansId) == null) {
                boolean f = fun2(urlstr, ansId, 0);
            } else {
                boolean f = fun2(urlstr, ansId, Integer.parseInt(map.get(ansId)));
            }
            if (err > 3) {
                System.out.println("ERR TOO MUCH!!");
                return;
            }
            if (n % 5 == 0) {
                System.out.println("更新commit完成:" + n + ", 更新行数:" + updates + ", 请求数:" + quest);
            }
//                FileUtil.updateLine(ansPath, ansId);

        }
        readit(next, map);
    }

    public static boolean fun2(String urls, String ansid, int ansidCount) throws Exception {

        boolean b = true;
        while (b) {
            JsonObject jo = HttpUtil.httpGet(urls);
            quest++;
            if (jo == null) {
                err++;
                return false;
            } else {
                err = 0;
            }
            urls = jo.get("paging").getAsJsonObject().get("next").getAsString();
            JsonArray anses = jo.get("data").getAsJsonArray();
            int common_counts = jo.get("common_counts").getAsInt();
            String v = Integer.toString(common_counts);
            map.put(ansid, v);
            if (ansidCount >= common_counts) {
                return true;
            }
            if (common_counts == ansidCount) {
                return true;
            }
            for (JsonElement ans : anses) {
                ansidCount++;
                String name = ans.getAsJsonObject().get("author").getAsJsonObject().get("member").getAsJsonObject().get("name").getAsString();
                String userId = ans.getAsJsonObject().get("author").getAsJsonObject().get("member").getAsJsonObject().get("id").getAsString();
                String content = ans.getAsJsonObject().get("content").getAsString();
                if (!userId.equals("0")) {
                    if (userId.equals(map2.get(ansid))) {
                        continue;
                    }
                    map2.put(ansid, userId);
                    FileUtil.writeFile(commitPath, ansid + "\t" + name + "\t" + userId+ "\t" + content);
                    updates++;
                    if (blackList.contains(userId)) {
                        isTarget = true;
                        System.err.println("******************"+ansid + "\t" + name + "\t" + userId+ "\t" + content);
                    }
//                System.out.println(ansid + "\t" + name + "\t" + userId);
                }
            }
            b = !jo.get("paging").getAsJsonObject().get("is_end").getAsBoolean();
        }
        return true;
    }

}
