package pers.acp.test.application.test;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

/**
 * @author zhangbin by 2018-1-31 22:00
 * @since JDK 11
 */
@WebService(endpointInterface = "pers.acp.test.application.test.ITestWebService", serviceName = "test", portName = "testport", targetNamespace = "http://acp.test/")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class TestWebService implements ITestWebService {

    @Override
    public int test1(int value1, int value2) {
        return value1 + value2;
    }

    @Override
    public int test2(int value1, int value2) {
        return value1 * value2;
    }

    @Override
    public String getServiceName() {
        return "test";
    }

}
