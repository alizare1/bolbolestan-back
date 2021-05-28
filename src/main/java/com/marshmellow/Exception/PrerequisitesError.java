package com.marshmellow.bolbolestan.Exception;

public class PrerequisitesError extends Exception {
    public PrerequisitesError(String preCode, String currCode) {
        super("Prerequisites Error: Student hasn't passed " + preCode + " for " + currCode);
    }
}
