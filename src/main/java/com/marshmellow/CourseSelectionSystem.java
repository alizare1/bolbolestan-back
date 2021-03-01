package com.marshmellow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.*;

import java.util.ArrayList;
import java.util.HashMap;


public class CourseSelectionSystem {

    private HashMap<String,Student> students;
    private HashMap<String,Offering> offerings;
    private HashMap<String, ArrayList<Offering>> courses;

    public CourseSelectionSystem(){
        this.students = new HashMap<>();
        this.offerings = new HashMap<>();
        this.courses = new HashMap<>();
    }

    private Student findStudent(String studentCode)throws StudentNotFound{

        if(students.get(studentCode) == null)
            throw new StudentNotFound();
        else
            return students.get(studentCode);
    }

    private Offering findOffering(String OfferingCode)throws OfferingNotFound {
        Offering offering = offerings.get(OfferingCode);
        if(offering != null)
            return offering;
        else
            throw new OfferingNotFound();
    }

    private JsonNode createJsonResult(JsonNode data) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("success", true);
        root.set("data", data);
        return root;
    }

    private JsonNode createJsonResult(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("success", true);
        root.put("data", data);
        return root;
    }

    private JsonNode createJsonResult(Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("success", false);
        root.put("error", e.getMessage());
        return root;
    }

    private void printJson(JsonNode json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public ObjectNode getWeeklyScheduleObjectNode(ArrayNode schedule){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode weeklySchedule = mapper.createObjectNode();
        weeklySchedule.set("weeklySchedule",schedule);
        return weeklySchedule;
    }
    
    private ArrayNode getOfferingJSONArray(){
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode coursesArr = objectMapper.createArrayNode();
        for(String key : courses.keySet()){
            for(Offering offering : courses.get(key)){
                ObjectNode offeringDetails = objectMapper.valueToTree(offering);
                offeringDetails.remove("units");
                offeringDetails.remove("capacity");
                offeringDetails.remove("classTime");
                offeringDetails.remove("examTime");
                offeringDetails.remove("prerequisites");
                coursesArr.add(offeringDetails);
            }
        }
        return coursesArr;
    }

    public void addOffering(Offering newOffer){
        String code =  newOffer.getCode();
        offerings.put(newOffer.getCode(),newOffer);
        String offerName = newOffer.getName();
        if(!courses.containsKey(offerName))
            courses.put(offerName,new ArrayList<Offering>());
        courses.get(offerName).add(newOffer);

        printJson(createJsonResult("offering with code " + code  +" added successfully!"));
    }

    public void addStudent(Student newStudent){
        String id = newStudent.getStudentId();
        students.put(id, newStudent);
        printJson(createJsonResult("student with id " + id + " added successfully!"));

    }

    public void getAllOfferings(String currStudentCode){
        try{
            findStudent(currStudentCode);
            ObjectMapper mapper = new ObjectMapper();
            printJson(createJsonResult(getOfferingJSONArray()));
        }catch(StudentNotFound exp){
            printJson(createJsonResult(exp));
        }

    }

    public void getOffering(String currStudentCode,String offeringCode){
        try{
            findStudent(currStudentCode);
            Offering offering = findOffering(offeringCode);
            ObjectMapper mapper = new ObjectMapper();
            printJson(createJsonResult(mapper.valueToTree(offering)));

        }catch(StudentNotFound | OfferingNotFound exp){
            printJson(createJsonResult(exp));
        }
    }

    public void addtoWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.addOffering(findOffering(offeringCode));
            printJson(createJsonResult("offering with code "+ offeringCode +
                    " added to weekly schedule successfully!"));
        }catch(StudentNotFound | OfferingNotFound exp){
            printJson(createJsonResult(exp));
        }
    }

    public void removeFromWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.removeOffering(findOffering(offeringCode));
            printJson(createJsonResult("offering with code " + offeringCode +
                    " removed from weekly schedule successfully!"));
        }catch(StudentNotFound | OfferingNotFound exp){
            printJson(createJsonResult(exp));
        }
    }

    public void getWeeklySched(String studentCode){
        try{
            ArrayNode schedule = findStudent(studentCode).getSchedule();
            printJson(createJsonResult(schedule));
        }catch(StudentNotFound exp){
            printJson(createJsonResult(exp));
        }

    }

    public void finalize(String studentCode){
        try{
            Student student = findStudent(studentCode);
            student.finalizeSelection();
            printJson(createJsonResult("weekly schedule finalized successfully!"));
        }catch(Exception exp){
            printJson(createJsonResult(exp));
        }
    }

}