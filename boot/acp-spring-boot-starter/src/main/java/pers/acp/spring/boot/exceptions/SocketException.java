package pers.acp.spring.boot.exceptions;

import pers.acp.core.exceptions.base.BaseException;

public class SocketException extends BaseException  {

	private static final long serialVersionUID = 4583362748825068914L;

	protected SocketException(String message) {
		super(message);
	}

}
