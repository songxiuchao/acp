import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.client.http.ResponseResult;
import pers.acp.client.socket.tcp.TcpClient;
import pers.acp.client.socket.udp.UdpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/6/1.
 * 客户端测试demo
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            final int x = i + 1;
            new Thread(() -> {
                for (int j = 0; j < 1; j++) {
                    int flag = 1;
                    if (flag == 1) {
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode body = mapper.createObjectNode();
                        body.put("param1", "尼玛");
                        body.put("param2", Integer.valueOf(x + "" + j));

                        Map<String, String> map = new HashMap<>();
                        map.put("grant_type", "client_credentials");
                        map.put("client_id", "test");
                        map.put("client_secret", "test");
                        long begin = System.currentTimeMillis();
                        ResponseResult recevStr = null;
                        try {
                            recevStr = new HttpClientBuilder().build()
                                    .url("http://127.0.0.1:8770/oauth/2.0/oauth/token")
                                    .doPost(map);
//                                    .doPostJSONStr(body.toString());
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                        System.out.println(recevStr);
                        System.out.println(x + "" + j + "----->" + (System.currentTimeMillis() - begin));
                    } else if (flag == 2) {
//                        SftpClient client = new SftpClient("115.159.227.180", 22, "root", "Pa88wordxstar");
//                        client.setRemotePath("/usr");
//                        client.setFileName("测试.txt");
//                        client.doUploadForSFTP(new File("C:\\WorkFile\\工作资料\\区块链\\服务器信息.txt"));
                    } else if (flag == 3) {
                        TcpClient client = new TcpClient("192.168.19.1", 1234, 60000, 600000);
//                        client.setServerCharset("gbk");
                        client.doSendSync("你是猪", true);
                        System.out.println(client.doSendSync("你是猪", true));
                    } else if (flag == 4) {
                        UdpClient client = new UdpClient("127.0.0.1", 9999, 60000);
//                        client.setServerCharset("gbk");
//                        client.doSend("你是猪", true);
                        System.out.println(client.doSendSync("你是猪", true));
                    }
                }
            }).start();
        }
    }
}
