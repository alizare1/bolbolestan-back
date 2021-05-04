package com.marshmellow.model;

public class Grade {
    public String code;
    public String name;
    public int grade;
    public int term;
    public int units;

    public Grade(String code, String name, int grade, int term, int units) {
        this.code = code;
        this.name = name;
        this.grade = grade;
        this.term = term;
        this.units = units;
    }
}
