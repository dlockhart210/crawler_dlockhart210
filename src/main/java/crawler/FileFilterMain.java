package crawler;

import util.FileUtil;

/**
 * @author dumengjie
 * @description
 * @create 2019-08-22
 */
public class FileFilterMain {
    static String filepath = "/Users/mengjiedu/Downloads/";

    public static void main(String[] args) {
        FileUtil.deleteLineByContains(filepath+"download (9)","tair ");
    }
}
