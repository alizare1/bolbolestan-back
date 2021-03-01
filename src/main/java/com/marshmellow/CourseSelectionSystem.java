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

    public JsonNode addOffering(Offering newOffer){
        String code =  newOffer.getCode();
        offerings.put(newOffer.getCode(),newOffer);
        String offerName = newOffer.getName();
        if(!courses.containsKey(offerName))
            courses.put(offerName,new ArrayList<Offering>());
        courses.get(offerName).add(newOffer);

        return createJsonResult("offering with code " + code  +" added successfully!");
    }

    public JsonNode addStudent(Student newStudent){
        String id = newStudent.getStudentId();
        students.put(id, newStudent);
        return createJsonResult("student with id " + id + " added successfully!");

    }

    public JsonNode getAllOfferings(String currStudentCode){
        try{
            findStudent(currStudentCode);
            return createJsonResult(getOfferingJSONArray());
        }catch(StudentNotFound exp){
            return createJsonResult(exp);
        }

    }

    public JsonNode getOffering(String currStudentCode,String offeringCode){
        try{
            findStudent(currStudentCode);
            Offering offering = findOffering(offeringCode);
            ObjectMapper mapper = new ObjectMapper();
            return createJsonResult(mapper.valueToTree(offering));

        }catch(StudentNotFound | OfferingNotFound exp){
            return createJsonResult(exp);
        }
    }

    public JsonNode addtoWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.addOffering(findOffering(offeringCode));
            return createJsonResult("offering with code "+ offeringCode +
                    " added to weekly schedule successfully!");
        }catch(StudentNotFound | OfferingNotFound exp){
            return createJsonResult(exp);
        }
    }

    public JsonNode removeFromWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.removeOffering(findOffering(offeringCode));
            return createJsonResult("offering with code " + offeringCode +
                    " removed from weekly schedule successfully!");
        }catch(StudentNotFound | OfferingNotFound exp){
            return createJsonResult(exp);
        }
    }

    public JsonNode getWeeklySched(String studentCode){
        try{
            ArrayNode schedule = findStudent(studentCode).getSchedule();
            return createJsonResult(schedule);
        }catch(StudentNotFound exp){
            return createJsonResult(exp);
        }

    }

    public JsonNode finalize(String studentCode){
        try{
            Student student = findStudent(studentCode);
            student.finalizeSelection();
            return createJsonResult("weekly schedule finalized successfully!");
        }catch(Exception exp){
            return createJsonResult(exp);
        }
    }

}