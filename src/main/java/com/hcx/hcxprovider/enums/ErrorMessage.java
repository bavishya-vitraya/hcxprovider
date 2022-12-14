package com.hcx.hcxprovider.enums;

public enum ErrorMessage {
    RESOURCE_NOT_FOUND("2000","PreAuthRequest Not Found"),
    RABBITMQ_ERROR("2001","error in pushing message to queue"),

    INVALID_TOKEN("2002","error invalid token ");


    private final String errorCode;
    private final String errorMessage;


    ErrorMessage(String errorCode, String errorMessage ) {
        this.errorCode = errorCode;
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public String getErrorCode() {
        return errorCode;
    }

}
