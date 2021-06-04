package com.ss.TIW_2021project.business.Exceptions;

public class DAOException extends Exception {

    public static final int _ERROR_PREPARING_QUERY = 1;
    public static final int _FAIL_TO_INSERT = 2;
    public static final int _UPDATE_FAILED = 3;
    public static final int _FAIL_TO_RETRIEVE = 4;
    public static final int _SQL_ERROR = 5;
    public static final int _ERROR_FINALIZING_CALL = 6;


    public static final int _ERROR_GETTING_CONN = 10;
    public static final int _ERROR_RELEASING_CONN = 11;

    private final int errorCode;

    public DAOException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
