package com.marshmellow.bolbolestan.Exception;

public class MinimumUnitsError extends Exception {
    public MinimumUnitsError() {
        super("You need at least 12 units to finalize");
    }
}
