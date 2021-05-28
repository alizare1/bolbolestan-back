package com.marshmellow.bolbolestan.Exception;

public class ClassTimeCollisionError extends Exception {
    public ClassTimeCollisionError(String code1, String code2) {
        super("Class " + code1 + " collides with class " + code2);
    }
}
