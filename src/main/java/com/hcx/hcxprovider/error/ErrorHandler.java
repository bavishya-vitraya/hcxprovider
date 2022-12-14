package com.hcx.hcxprovider.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ProviderException.class)
    public ResponseEntity handleException(ProviderException e) {
        // log exception
        Map<String,Object> body= new HashMap<>();
        body.put("timestamp",new Date());
        body.put("status",400);
        body.put("erroMessage",e.getErrorMessage());

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity handleTokenException(InvalidTokenException e) {
        // log exception
        Map<String,Object> body= new HashMap<>();
        body.put("timestamp",new Date());
        body.put("status",403);
        body.put("erroMessage",e.getErrorMessage());

        return new ResponseEntity<>(body,HttpStatus.FORBIDDEN);

    }
}
