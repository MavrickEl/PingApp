package com.example.pingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelException extends IllegalArgumentException {
    public ChannelException(String message) {
        super(message);
    }
}