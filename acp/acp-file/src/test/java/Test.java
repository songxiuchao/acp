import java.io.*;

/**
 * Create by zhangbin on 2017-11-11 21:53
 */
public class Test {

    public static void main(String[] args) throws IOException {
        // 读文件内容，存入本地变量 content
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\WorkFile\\11.txt"), "gbk"));
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            builder.append(str).append("\r\n");
        }
        String content = builder.toString();

        // 将字符串 content 的内容写入新文件
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\WorkFile\\22.txt"), "gbk"));
        writer.write(content);
        writer.flush();
    }

}
