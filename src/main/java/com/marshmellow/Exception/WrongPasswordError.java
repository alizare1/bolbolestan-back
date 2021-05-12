package com.marshmellow.Exception;

public class WrongPasswordError extends Exception {
    public WrongPasswordError() {
        super("Wrong password!");
    }
}
