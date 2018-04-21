package pers.acp.springboot.core.exceptions;

import pers.acp.core.exceptions.base.BaseException;

public class ServerException extends BaseException {

    private static final long serialVersionUID = -826325914171274124L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Integer code, String message) {
        super(code, message);
    }

}
