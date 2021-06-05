package com.ss.TIW_2021project.business.Exceptions;

public class ServiceException extends Exception {

    public static final int _FAILED_TO_CHECK_CREDENTIALS = 1;
    public static final int _FAILED_TO_PLACE_ORDER = 3;
    public static final int _FAILED_TO_RETRIEVE_ORDERS = 4;
    public static final int _FAILED_TO_RETRIEVE_PRODUCTS_INFO = 5;
    public static final int _FAILED_TO_RETRIEVE_SUPPLIERS_INFO = 6;
    public static final int _FAILED_TO_RETRIEVE_USERS_INFO = 7;
    public static final int _FAILED_TO_UPDATE_USERS_LAST_PRODUCTS = 8;
    public static final int _FAILED_TO_CHECK_UNIQUE_EMAIL = 9;


    private final int errorCode;

    public ServiceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
