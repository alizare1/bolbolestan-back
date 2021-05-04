package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ExamTime {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date end;

    public boolean collides(ExamTime other) {
        return this.start.compareTo(other.end) < 0 && this.end.compareTo(other.start) > 0;
    }

    @Override
    public String toString() {
        return "ExamTime{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
