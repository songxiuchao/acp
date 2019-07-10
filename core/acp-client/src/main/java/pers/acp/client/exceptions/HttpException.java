package pers.acp.client.exceptions;

import pers.acp.core.base.BaseException;

public class HttpException extends BaseException {

    private static final long serialVersionUID = -3928925685922469420L;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Integer code, String message) {
        super(code, message);
    }

}
