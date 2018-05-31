import pers.acp.client.soap.WebServiceClient;
import pers.acp.packet.xml.SoapType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbin by 31/05/2018 14:48
 * @since JDK1.8
 */
public class TestWebService {

    public static void main(String[] args) {
        WebServiceClient webServiceClient = new WebServiceClient("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx");
        webServiceClient.setSoapType(SoapType.SOAP_1_2);
        webServiceClient.setNameSpace("http://WebXml.com.cn/");
        webServiceClient.setServiceName("WeatherWebService");
        webServiceClient.setPortName("WeatherWebServiceSoap12");
        webServiceClient.setMethodName("getWeatherbyCityName");
        Map<String, String> params = new HashMap<>();
        params.put("theCityName", "成都");
        webServiceClient.setPatameterMap(params);
//        webServiceClient.setPrefix("n1");
        webServiceClient.setReturnName("getWeatherbyCityNameResponse");
        String rtStr = webServiceClient.doCallWebService();
        System.out.println(rtStr);
    }

}
