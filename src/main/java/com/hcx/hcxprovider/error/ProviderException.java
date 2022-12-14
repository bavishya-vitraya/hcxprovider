package com.hcx.hcxprovider.error;

import com.hcx.hcxprovider.enums.ErrorMessage;

public class ProviderException extends Exception{

    private ErrorMessage error;

    public ProviderException(ErrorMessage error) {
        this.error = error;
    }

    public String getErrorCode() {
        return error.getErrorCode();
    }

    public String getErrorMessage() {

        return error.getErrorMessage();
    }
}
