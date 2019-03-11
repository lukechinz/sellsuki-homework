package com.sellsuki.homework.types;

public enum ErrorType {

    PRODUCT_NOT_FOUND                                  ("4001", "Product not found"),
    CANNOT_GET_BOOK_LIST                               ("4002", "Cannot get book list from api"),
    INTERNAL_SERVER_ERROR                              ("500", "Internal server error");

    private String code;
    private String message;


    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
