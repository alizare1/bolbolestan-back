package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.marshmellow.Exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String studentId;
    private String name;
    private String secondName;
    private String email;
    private String password;
    private String birthDate;
    private String field;
    private String faculty;
    private String level;
    private String status;
    private String img;
    private Map<String, Grade> grades = new HashMap<>();

    @JsonIgnore
    public ArrayList<Grade> getGrades() {
        return new ArrayList<>(grades.values());
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

    @JsonIgnore
    public void setGrades(ArrayList<Grade> gradesArr) {
        for (Grade grade : gradesArr) {
            grades.put(grade.code, grade);
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                '}';
    }
}
