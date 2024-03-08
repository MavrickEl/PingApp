package com.example.pingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RuleException extends IllegalArgumentException {
    public RuleException(String message) {
        super(message);
    }
}
