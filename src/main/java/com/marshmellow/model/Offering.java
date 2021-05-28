package com.marshmellow.bolbolestan.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Offering {
    private String code;
    private String classCode;
    private String name;
    private String instructor;
    private int units;
    private String type;
    private ClassTime classTime = new ClassTime();
    private ExamTime examTime = new ExamTime();
    private int capacity;
    private ArrayList<String> prerequisites;
    private int participantsCount = 0;

    public boolean classTimeCollidesWith(Offering other) {
        return this.getClassTime().collides(other.getClassTime());
    }

    public boolean examTimeCollidesWith(Offering other) {
        return this.getExamTime().collides(other.getExamTime());
    }

    public int getUnits() {
        return units;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public ClassTime getClassTime() {
        return classTime;
    }

    @JsonIgnore
    public ArrayList<String> getClassDays() {
        return classTime.days;
    }

    @JsonIgnore
    public void setClassDays(ArrayList<String> days) {
        classTime.days = days;
    }

    @JsonIgnore
    public String getClassHour() {
        return classTime.time;
    }

    @JsonIgnore
    public void setClassHour(String time) {
        classTime.time = time;
    }

    public void setClassTime(ClassTime classTime) {
        this.classTime = classTime;
    }

    public ExamTime getExamTime() {
        return examTime;
    }

    @JsonIgnore
    public Date getExamStart() {
        return examTime.start;
    }

    @JsonIgnore
    public Date getExamEnd() {
        return examTime.end;
    }

    public void setExamTime(ExamTime examTime) {
        this.examTime = examTime;
    }

    @JsonIgnore
    public void setExamStart(Date date) {
        examTime.start = date;
    }

    @JsonIgnore
    public void setExamEnd(Date date) {
        examTime.end = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public ArrayList<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(ArrayList<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Offering))
            return false;

        Offering other = (Offering) o;
        return code.equals(other.getCode()) && classCode.equals(other.getClassCode());
    }

    @Override
    public String toString() {
        return "Offering{" +
                "code='" + code + '\'' +
                ", classCode='" + classCode + '\'' +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                ", units=" + units +
                ", type='" + type + '\'' +
                ", classTime=" + classTime +
                ", examTime=" + examTime +
                ", capacity=" + capacity +
                ", prerequisites=" + prerequisites +
                '}';
    }
}
