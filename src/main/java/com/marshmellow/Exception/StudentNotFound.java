package com.marshmellow.Exception;

public class StudentNotFound extends Exception {
    public StudentNotFound() {
        super("No student found with this code");
    }
}
