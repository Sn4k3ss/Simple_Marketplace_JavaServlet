package com.ss.TIW_2021project.business.Exceptions;

public class UtilityException extends Exception {

    public static final int _ERROR_GETTING_CONN = 1;
    public static final int _ERROR_CLOSING_CONN = 2;
    public static final int _ERROR_GETTING_PROD_FROM_REQ = 3;

    private int errorCode;

    public UtilityException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
