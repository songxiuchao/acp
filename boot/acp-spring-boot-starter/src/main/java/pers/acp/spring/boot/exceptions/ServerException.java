package pers.acp.spring.boot.exceptions;

import pers.acp.core.base.BaseException;
import pers.acp.spring.boot.enums.ResponseCode;

public class ServerException extends BaseException {

    public ServerException(ResponseCode responseCode) {
        super(responseCode.getValue(), responseCode.getName());
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Integer code, String message) {
        super(code, message);
    }

}
