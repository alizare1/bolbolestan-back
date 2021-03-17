package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.marshmellow.Exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class Grade {
    public String code;
    public int grade;
    public int units;

    public Grade(String code, int grade, int units) {
        this.code = code;
        this.grade = grade;
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
    private  ArrayList<Offering> submittedCourses = new ArrayList<>();

    public void addOffering(Offering offering) throws  OfferingNotFound, ExamTimeCollisionError, ClassTimeCollisionError {
      //  validatePrerequisites(offering);
        schedule.addOffering(offering);
    }

    public void validatePrerequisites(Offering offering) throws PrerequisitesError {
        for (String prerequisite : offering.getPrerequisites())
            if (!grades.containsKey(prerequisite) || grades.get(prerequisite).grade < 10)
                throw new PrerequisitesError(prerequisite);
    }

   /* public void (Offering offering)throws {
        for(String courseCode : grades.keySet()){
            if(offering.getCode().equals(courseCode) && grades.get(courseCode).grade >= 10)
                throw new
        }
    }*/

    public void removeOffering(Offering offering) throws OfferingNotFound {
        schedule.removeOffering(offering);
    }

    public ArrayList<Offering> getSchedule() {
        return schedule.getSchedule();
    }

    public void finalizeSelection() throws MinimumUnitsError, MaximumUnitsError,
            CapacityError,PrerequisitesError{
        schedule.finalizeSelection(this);
        submittedCourses = getSchedule();

    }

    public ArrayList<Offering> getRemovedCourses(){
        ArrayList<Offering> differences = new ArrayList(submittedCourses);
        differences.removeAll(getSchedule());
        return differences;
    }
    public void resetSchedule(){


    }

    public int getCurrentUnitCount() {
        return schedule.getUnitCount();
    }

    public float getGpa() {
        float gradeSum = 0;
        for (Grade courseGrade : grades.values())
            gradeSum += courseGrade.grade * courseGrade.units;
        return gradeSum / getPassedUnitsCount();
    }

    public int getPassedUnitsCount() {
        int unitSum = 0;
        for (Grade courseGrade : grades.values())
            unitSum += courseGrade.units;
        return unitSum;
    }

    public Collection<Grade> getGrades() {
        return grades.values();
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