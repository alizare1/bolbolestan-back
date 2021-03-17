package com.marshmellow.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.marshmellow.Exception.*;

public class CourseSelectionSystem {

    private static final String API_COURSES = "http://138.197.181.131:5000/api/courses";
    private static final String API_STUDENTS = "http://138.197.181.131:5000/api/students";
    private static final String API_GRADE = "http://138.197.181.131:5000/api/grades/";

    private final HashMap<String,Student> students;
    private final Map<String, Map<String,Offering>> courses;
    private Student currentStudent;

    private static CourseSelectionSystem instance;

    public static CourseSelectionSystem getInstance(){

        if (instance == null)
            instance = new CourseSelectionSystem();
        return instance;
    }

    public Student getCurrentStudent(){
        return currentStudent;
    }

    public void setCurrentStudent(Student std){
        this.currentStudent = std;
    }

    private CourseSelectionSystem(){
        this.students = new HashMap<>();
        this.courses = new HashMap<>();
        try {
            Offering[] courses = getOfferingsFromAPI();
            Student[] students = getStudentsFromAPI();

            this.addCourses(courses);
            for (Student student : students)
                setGrades(student);
            this.addStudents(students);
        } catch(IOException | InterruptedException exp){
            exp.printStackTrace();
        }
    }

    private Student[] getStudentsFromAPI() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var studentsReq = HttpRequest.newBuilder(
                URI.create(API_STUDENTS)
        ).build();

        HttpResponse<String> studentsRes = client.send(studentsReq, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(studentsRes.body(), Student[].class);
    }

    private Offering[] getOfferingsFromAPI() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var coursesReq = HttpRequest.newBuilder(
                URI.create(API_COURSES)
        ).build();

        HttpResponse<String> coursesRes = client.send(coursesReq, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(coursesRes.body(), Offering[].class);
    }

    private void setGrades(Student student) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        var gradesReq = HttpRequest.newBuilder(
                URI.create(API_GRADE + student.getStudentId())
        ).build();
        HttpResponse<String> gradesRes = client.send(gradesReq, HttpResponse.BodyHandlers.ofString());
        JsonNode gradesArr = objectMapper.readTree(gradesRes.body());

        Map<String, Grade> gradesMap = new HashMap<>();
        gradesArr.forEach(grade -> {
            try {
                int unitCount = this.getCourse(grade.get("code").asText(), "01").getUnits();
                Grade newGrade = new Grade(grade.get("code").asText(), grade.get("grade").asInt(), unitCount);
                gradesMap.put(grade.get("code").asText(), newGrade);
            } catch (OfferingNotFound offeringNotFound) {
                offeringNotFound.printStackTrace();
            }
        });
        student.setGrades(gradesMap);
    }


    public Offering getCourse(String code, String classCode) throws OfferingNotFound {
        if (!courses.containsKey(code) || !courses.get(code).containsKey(classCode))
            throw new OfferingNotFound();

        return courses.get(code).get(classCode);
    }

    public ArrayList<Offering> getCourses(String filter) {
        ArrayList<Offering> coursesArray = new ArrayList<>();
        for (Map<String, Offering> offers : courses.values()) {
             for(Offering offer : offers.values())
                 if (offer.getName().contains(filter)) {
                     coursesArray.addAll(offers.values());
                     break;
                 }
        }
        // coursesArray.addAll(offer.values());


        return coursesArray;
    }

    public Student getStudent(String studentCode)throws StudentNotFound{

        if(students.get(studentCode) == null)
            throw new StudentNotFound();
        else
            return students.get(studentCode);
    }

    public void addStudents(Student[] students) {
        for (Student student : students)
            this.students.put(student.getStudentId(), student);
    }

    public void addCourses(Offering[] courses) {
        for (Offering offering : courses) {
            if(!this.courses.containsKey(offering.getCode()))
                this.courses.put(offering.getCode(), new HashMap<>());
            this.courses.get(offering.getCode()).put(offering.getClassCode(), offering);
        }
    }

    public void addtoWeeklySched(String studentCode, String offeringCode, String groupCode)
            throws OfferingNotFound, StudentNotFound, PrerequisitesError, ClassTimeCollisionError, ExamTimeCollisionError {

        getStudent(studentCode).addOffering(getCourse(offeringCode, groupCode));
    }

    public void removeFromWeeklySched(String studentCode, String offeringCode, String groupCode)
            throws OfferingNotFound, StudentNotFound {

        getStudent(studentCode).removeOffering(getCourse(offeringCode, groupCode));
    }

    public ArrayList<Offering> getWeeklySched(String studentCode) throws StudentNotFound {
        return getStudent(studentCode).getSchedule();
    }


}
