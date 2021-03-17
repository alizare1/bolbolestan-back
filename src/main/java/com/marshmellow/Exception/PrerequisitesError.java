package com.marshmellow.Exception;

public class PrerequisitesError extends Exception {
    public PrerequisitesError(String classCode) {
        super("Prerequisites Error: " + classCode);
    }
}
