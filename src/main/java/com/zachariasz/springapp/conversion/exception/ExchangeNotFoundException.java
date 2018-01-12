package com.zachariasz.springapp.conversion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExchangeNotFoundException extends Exception {
    public ExchangeNotFoundException(String message){
        super("Not found: " + message);
    }
}
