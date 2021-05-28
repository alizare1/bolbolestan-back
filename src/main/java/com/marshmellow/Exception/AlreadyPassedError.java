package com.marshmellow.bolbolestan.Exception;

public class AlreadyPassedError extends Exception {
    public AlreadyPassedError(String classCode) {
        super("Student has passed " + classCode);
    }
}
