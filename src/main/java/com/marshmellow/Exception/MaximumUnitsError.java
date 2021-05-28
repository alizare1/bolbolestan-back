package com.marshmellow.bolbolestan.Exception;

public class MaximumUnitsError extends Exception {
    public MaximumUnitsError() {
        super("You can't acquire more than 20 Units");
    }
}
