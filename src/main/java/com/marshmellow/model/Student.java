package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.marshmellow.Exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Grade {
    public String code;
    public int grade;
    public int term;
    public int units;

    public Grade(String code, int grade, int term, int units) {
        this.code = code;
        this.grade = grade;
        this.term = term;
        this.units = units;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String studentId;
    private String name;
    private String secondName;
    private String birthDate;
    private Map<String, Grade> grades;
    private final WeeklySchedule schedule = new WeeklySchedule();

    public void addOffering(Offering offering) throws  OfferingNotFound, ExamTimeCollisionError, ClassTimeCollisionError {
        schedule.addOffering(offering);
    }

    public void validatePrerequisites(Offering offering) throws PrerequisitesError {
        for (String prerequisite : offering.getPrerequisites())
            if (!grades.containsKey(prerequisite) || grades.get(prerequisite).grade < 10)
                throw new PrerequisitesError(prerequisite);
    }

    public boolean hasPassed(Offering offering) {
        if (grades.containsKey(offering.getCode()))
            return grades.get(offering.getCode()).grade >= 10;
        return false;
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        schedule.removeOffering(offering);
    }

    public ArrayList<Offering> getSchedule() {
        return schedule.getSchedule();
    }

    public ArrayList<Offering> getInProgressSchedule() {
        return schedule.getInProgressCourses();
    }

    public void finalizeSelection() throws MinimumUnitsError, MaximumUnitsError,
            CapacityError, PrerequisitesError, AlreadyPassedError {
        schedule.finalizeSelection(this);
    }

    public void resetSelection() {
        schedule.resetSelection();
    }

    public int getCurrentUnitCount() {
        return schedule.getUnitCount();
    }

    public float getGpa() {
        float gradeSum = 0;
        int unitSum = 0;
        for (Grade courseGrade : grades.values()) {
            gradeSum += courseGrade.grade * courseGrade.units;
            unitSum += courseGrade.units;
        }
        return gradeSum / unitSum;
    }

    public int getPassedUnitsCount() {
        int unitSum = 0;
        for (Grade courseGrade : grades.values()) {
            if (courseGrade.grade >= 10)
                unitSum += courseGrade.units;
        }
        return unitSum;
    }

    public Map<String, Integer> getGrades() {
        Map<String, Integer> gradeMap = new HashMap<>();
        for (String s : grades.keySet()) {
            gradeMap.put(s, grades.get(s).grade);
        }
        return gradeMap;
    }

    public void setGrades(Map<String, Grade> grades) {
        this.grades = grades;
    }

    @JsonProperty("id")
    public String getStudentId() {
        return studentId;
    }

    @JsonProperty("id")
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", secondName='" + secondName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
