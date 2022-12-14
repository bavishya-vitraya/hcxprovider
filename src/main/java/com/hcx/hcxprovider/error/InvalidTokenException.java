package com.hcx.hcxprovider.error;

import com.hcx.hcxprovider.enums.ErrorMessage;

public class InvalidTokenException extends RuntimeException{
    private ErrorMessage error;

    public InvalidTokenException( ErrorMessage error) {
        this.error = error;
    }

    public String getErrorCode() {
        return error.getErrorCode();
    }

    public String getErrorMessage() {

        return error.getErrorMessage();
    }

}
