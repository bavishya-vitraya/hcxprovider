package com.hcx.hcxprovider.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProviderException.class)
    public  ResponseEntity<?> handleProviderException(ProviderException exception){

        return new ResponseEntity<ProviderException>(exception,HttpStatus.BAD_REQUEST);
    }
}
