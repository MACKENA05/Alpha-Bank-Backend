package com.mackena.Banking_Application_backend.exceptions;

public class InvalidAccountException extends Throwable {
    public InvalidAccountException(String message) {
        super(message);
    }
}
