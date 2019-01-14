package pers.acp.core.exceptions;

import pers.acp.core.exceptions.base.BaseException;

public class EnumValueUndefinedException extends BaseException {

	private static final long serialVersionUID = 4033154834491165531L;

	public EnumValueUndefinedException(Class<?> enumType, Object value) {
		super(8010, enumType + " undefined value " + value);
	}

}
