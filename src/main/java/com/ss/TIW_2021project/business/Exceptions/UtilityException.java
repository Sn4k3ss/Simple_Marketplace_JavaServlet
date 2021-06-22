package com.ss.TIW_2021project.business.Exceptions;

public class UtilityException extends Exception {

    public static final int _ERROR_GETTING_CONN = 1;
    public static final int _ERROR_CLOSING_CONN = 2;
    public static final int _ERROR_CLOSING_RESULT_SET = 3;
    public static final int _ERROR_CLOSING_STATEMENT = 4;
    public static final int _ERROR_CREATING_CONN_POOL = 5;
    public static final int _CONNECTIONS_LIMIT_REACHED = 6;
    public static final int _CONNECTION_POOL_IS_NULL = 7;



    public static final int _ERROR_GETTING_PROD_FROM_REQ = 100;

    private final int errorCode;

    public UtilityException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
