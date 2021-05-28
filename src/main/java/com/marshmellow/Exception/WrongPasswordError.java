package com.marshmellow.bolbolestan.Exception;

public class WrongPasswordError extends Exception {
    public WrongPasswordError() {
        super("Wrong password!");
    }
}
