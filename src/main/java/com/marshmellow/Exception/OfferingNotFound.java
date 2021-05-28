package com.marshmellow.bolbolestan.Exception;

public class OfferingNotFound extends Exception {
    public OfferingNotFound() {
        super("No offering found with this code");
    }
}
