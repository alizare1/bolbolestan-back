package com.marshmellow.bolbolestan.Exception;

public class ExamTimeCollisionError extends Exception {
    public ExamTimeCollisionError(String code1, String code2) {
        super("Exams of class " + code1 + " and " + code2 + " collide");
    }
}
