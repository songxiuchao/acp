package pers.acp.test.application.test;

import pers.acp.webservice.base.IWebService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author zhangbin by 2018-1-31 22:05
 * @since JDK 11
 */
@WebService(targetNamespace = "http://acp.test/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ITestWebService extends IWebService {

    @WebMethod
    @WebResult(name = "return")
    int test1(@WebParam(name = "value1") int value1,
              @WebParam(name = "value2") int value2);

    @WebMethod
    @WebResult(name = "return")
    int test2(@WebParam(name = "value1") int value1,
              @WebParam(name = "value2") int value2);

}
