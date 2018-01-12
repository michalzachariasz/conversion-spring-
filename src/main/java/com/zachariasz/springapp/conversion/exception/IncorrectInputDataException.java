package com.zachariasz.springapp.conversion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncorrectInputDataException extends Exception {
    public IncorrectInputDataException(String message){
        super(message);
    }
}
