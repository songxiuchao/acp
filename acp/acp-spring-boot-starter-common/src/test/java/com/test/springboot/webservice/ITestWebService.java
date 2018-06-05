package com.test.springboot.webservice;

import pers.acp.springboot.core.soap.base.IWebService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author zhangbin
 * @date 2018-1-15 10:42
 * @since JDK1.8
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
