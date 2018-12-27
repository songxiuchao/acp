package pers.acp.springboot.core.tools;

import pers.acp.core.CommonTools;
import pers.acp.springboot.core.enums.ResponseCode;
import pers.acp.springboot.core.vo.ErrorVO;

/**
 * Created by Shepherd on 2016-08-05.
 * 报文工具类
 */
public class PackageTools {

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @return 响应报文JSON对象
     */
    public static ErrorVO buildErrorResponsePackage(ResponseCode responseCode, String msg) {
        if (CommonTools.isNullStr(msg)) {
            msg = responseCode.getName();
        }
        return buildErrorResponsePackage(responseCode.getValue(), msg);
    }

    /**
     * 构建响应报文
     *
     * @param code 响应代码
     * @param msg  响应信息
     * @return 响应报文JSON对象
     */
    public static ErrorVO buildErrorResponsePackage(Integer code, String msg) {
        ErrorVO errorVO = new ErrorVO();
        errorVO.setCode(code);
        errorVO.setError(msg);
        errorVO.setErrorDescription(msg);
        return errorVO;
    }

}
