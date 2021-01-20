package util;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dumengjie
 * @description
 * @create 2019-08-04
 */
public class FileUtil {

    public static void saveMap(String path, Map<String, String> map) {
        File inFile = new File(path);
        inFile.delete();

        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(path, true);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writer.write("\n" + entry.getKey() + "\t" + entry.getValue());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, String> getFirstAndI(String pathname,int i) {
        /* 读入TXT文件 */
        Map<String, String> map = new HashMap();
        File filename = new File(pathname); // 要读取以上路径的input。txt文件
        InputStreamReader reader = null; // 建立一个输入流对象reader
        try {
            reader = new InputStreamReader(
                    new FileInputStream(filename));

            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                if (line == null) {
                    continue;
                }
                String[] str = line.split("\t");
                if (str.length < 2) {
                    continue;
                }
                String k = map.get(str[0]);
                map.put(str[0], str[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    public static void writeFile(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write("\n" + content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void deleteLineByContains(String path, String args) {
        File inFile = new File(path);
        File outFile = new File(path + ".bak");

        BufferedReader br = null;
        String readedLine;
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            br = new BufferedReader(new FileReader(inFile));
            int idx = 0;
            while ((readedLine = br.readLine()) != null) {
                if (readedLine.contains(args)) {
                    continue;
                }
                bw.write(readedLine + "\n");
                if (idx++ == 100) {
                    bw.flush();
                    idx = 0;
                }
            }
            bw.flush();
            inFile.delete();
            outFile.renameTo(inFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}