package com.hcx.hcxprovider.error;

public class ProviderException extends Exception{

    private ErrorMessage message;

    public ProviderException(ErrorMessage message) {
        super(message.getErrorMessage());
    }
}
