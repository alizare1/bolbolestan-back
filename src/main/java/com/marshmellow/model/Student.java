package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.marshmellow.Exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Grade {
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String studentId;
    private String name;
    private String secondName;
    private String birthDate;
    private String field;
    private String faculty;
    private String level;
    private String status;
    private String img;
    private Map<String, Grade> grades;
    private final WeeklySchedule schedule = new WeeklySchedule();

    public void addOffering(Offering offering) throws Exception {
        if (!offering.hasCapacity())
            addToQueue(offering);
        else
            schedule.addOffering(offering);
    }

    public void addToQueue(Offering offering) throws Exception {
        schedule.addToQueue(offering);
    }

    public void submitFromQueue(Offering offering) {
        schedule.submitFromQueue(offering);
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

    public void removeOfferingFromQueue(Offering offering) throws OfferingNotFound {
        schedule.removeFromQueue(offering);
    }

    @JsonIgnore
    public WeeklySchedule getWeeklySchedule() {
        return schedule;
    }

    @JsonIgnore
    public ArrayList<Offering> getSchedule() {
        return schedule.getSchedule();
    }

    @JsonIgnore
    public ArrayList<Offering> getInProgressSchedule() {
        return schedule.getInProgressCourses();
    }

    @JsonIgnore
    public ArrayList<Offering> getInProgressQueue() {
        return schedule.getInProgressQueue();
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

    @JsonIgnore
    public Map<String, Integer> getGrades() {
        Map<String, Integer> gradeMap = new HashMap<>();
        for (String s : grades.keySet()) {
            gradeMap.put(s, grades.get(s).grade);
        }
        return gradeMap;
    }

    @JsonProperty("grades")
    public Map<Integer, ArrayList<Grade>> getDetailedGrades() {
        Map<Integer, ArrayList<Grade>> gradeMap = new HashMap<>();
        for (Grade grade : grades.values()) {
            if (!gradeMap.containsKey(grade.term))
                gradeMap.put(grade.term, new ArrayList<>());
            gradeMap.get(grade.term).add(grade);
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
