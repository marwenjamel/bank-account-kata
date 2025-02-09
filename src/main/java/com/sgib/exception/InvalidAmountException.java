package com.sgib.exception;

public class InvalidAmountException extends IllegalArgumentException{

    public InvalidAmountException(String message) {
        super(message);
    }
}