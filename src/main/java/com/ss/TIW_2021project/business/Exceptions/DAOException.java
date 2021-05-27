package com.ss.TIW_2021project.business.Exceptions;

public class DAOException extends Exception {

    public final static int _MALFORMED_QUERY = 1;
    public final static int _FAIL_TO_INSERT = 2;
    public final static int _UPDATE_FAILED = 3;
    public final static int _FAIL_TO_RETRIEVE = 4;
    public final static int _SQL_ERROR = 5;

    private int errorCode;

    public DAOException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
