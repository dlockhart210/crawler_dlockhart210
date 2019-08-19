package crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Map;

/**
 * @author dumengjie
 * @description
 * @create 2019-08-04
 */
public class ZH_V2 {
    static String filepath = "/Users/mengjiedu/Downloads/";
    static String ansPath = filepath + "zhihu_answers.txt";
    static String commitPath = filepath + "zhihu_commit.txt";
    static long quest=0;
    static long err=0;

//    更新commit完成:2000, quest:3721

//更新commit完成:1000, quest:1810
//更新commit完成:1000, quest:1613
//更新commit完成:1000, quest:1446
//更新commit完成:1000, quest:1389

//    更新commit完成:1000, quest:1196
//    更新commit完成:1000, quest:1351

    public static void main(String[] args) throws Exception {
        HttpUtil.cookie = "_zap=edbb8c61-7467-4063-a3c9-508b41862eb3; _xsrf=410e0b3a-9cbf-4502-8c51-9a0b5afe9b5f; d_c0=\"AOAgC8YO2A-PTv4IKB-vWpCcFoHLC3C5Ams=|1564977425\"; capsion_ticket=\"2|1:0|10:1565229095|14:capsion_ticket|44:N2JkNjg3NDFjYWJiNDVlMDhiMjc2YTE0MThlYTZiZDM=|6593e19e23119543960e42da6e59000df59f28b1b571dddbe7d6e9c8be111f97\"; z_c0=\"2|1:0|10:1565229119|4:z_c0|92:Mi4xVlUzWERnQUFBQUFBNENBTHhnN1lEeVlBQUFCZ0FsVk5QODQ0WGdEaHU2bXAwSWxtbVRCb2dLY3J3NEEyaGpwV29B|ff39fe9801927e6d643e0af9153d3d9c707ba4b64fd827351988b43bb4a689c2\"; tst=r; anc_cap_id=7d30761695384ce79098fa26ea3f020f; tgw_l7_route=060f637cd101836814f6c53316f73463";

//        updateAnsIdList(null, null);
        Thread.sleep(3000);
        getCommitListInit(1000);

        System.out.println(new Date().toString());
    }

    private static void getCommitListInit(int max) throws Exception {
        int n = 0;
        while (true) {
            if (n > max) {
                return;
            }
            n++;
            String ansId = FileUtil.getFirstLine(ansPath);
            if (ansId == null) {
                break;
            }
            String urlstr = "https://www.zhihu.com/api/v4/answers/" + ansId + "/comments?limit=20&offset=0&order=reverse&status=open";
            boolean f = getCommitList(urlstr, ansId);
            if (err > 10) {
                System.out.println("ERR TOO MUCH!!");
                return;
            }
            if (n % 10 == 0) {
                System.out.println("更新commit完成:" + n+", quest:"+quest);
            }
            FileUtil.updateLine(ansPath, ansId);
        }
    }


    public static boolean getCommitList(String urls, String ansid) throws Exception {
        JsonObject jo = HttpUtil.httpGet(urls);
        quest++;
        if (jo == null) {
            err++;
            return false;
        }else{
            err=0;
        }
        String next = jo.get("paging").getAsJsonObject().get("next").getAsString();
        JsonArray anses = jo.get("data").getAsJsonArray();
        for (JsonElement ans : anses) {
//            String content = ans.getAsJsonObject().get("content").getAsString();
            String name = ans.getAsJsonObject().get("author").getAsJsonObject().get("member").getAsJsonObject().get("name").getAsString();
            String userId = ans.getAsJsonObject().get("author").getAsJsonObject().get("member").getAsJsonObject().get("id").getAsString();
            if (!userId.equals("0")) {
                FileUtil.writeFile(commitPath, ansid + "\t" + name + "\t" + userId);
//                System.out.println(ansid + "\t" + name + "\t" + userId);
            }
        }
        boolean b = jo.get("paging").getAsJsonObject().get("is_end").getAsBoolean();
        if (!b) {
            getCommitList(next, ansid);
        }
        return true;
    }


    public static void updateAnsIdList(String s, Map<String, String> map) throws Exception {

        if (map == null) {
            map = FileUtil.getLineList(ansPath);
        }
        if (s == null) {
            s = "https://www.zhihu.com/api/v4/questions/275359100/answers?include=data%5B*%5D.is_normal%2Cadmin_closed_comment%2Creward_info%2Cis_collapsed%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cis_labeled%2Cis_recognized%2Cpaid_info%2Cpaid_info_content%3Bdata%5B*%5D.mark_infos%5B*%5D.url%3Bdata%5B*%5D.author.follower_count%2Cbadge%5B*%5D.topics&offset=100&limit=20&sort_by=updated";
        }
        JsonObject jo = HttpUtil.httpGet(s);
        String next = jo.get("paging").getAsJsonObject().get("next").getAsString();
        quest++;
        JsonArray anses = jo.get("data").getAsJsonArray();
        for (JsonElement ans : anses) {
            String ansId = ans.getAsJsonObject().get("id").getAsString();
            long updatetime = ans.getAsJsonObject().get("updated_time").getAsLong() * 1000;
            String updateTimestr = new Date(updatetime).toString();
            System.out.println(updateTimestr + "   " + ansId);
            String t = map.get(ansId);
            if (t != null && t.equals(String.valueOf(updatetime))) {
                System.out.println("update end!!");
                return;
            } else if (t != null) {
                FileUtil.deleteLine(ansPath, ansId);
            }
            FileUtil.writeFile(ansPath, ansId + "\t" + updatetime);
        }
        updateAnsIdList(next, map);
    }


}
