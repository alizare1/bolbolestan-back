package com.marshmellow.bolbolestan.Exception;

public class CapacityError extends Exception {
    public CapacityError(String classCode) {
        super("Class " + classCode + " is Full");
    }
}
