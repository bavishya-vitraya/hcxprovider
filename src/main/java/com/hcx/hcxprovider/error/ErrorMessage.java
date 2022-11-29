package com.hcx.hcxprovider.error;

public enum ErrorMessage {

    REQUEST_NOT_FOUND("request  not found", "PV.001");
    private String errorMessage;
    private String errorCode;

    ErrorMessage(String errorMessage,String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
