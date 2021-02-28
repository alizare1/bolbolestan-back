package com.marshmellow;

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

    public void printResult(Boolean success,Object data,String error){
        String message;
        if(success)
            message = "\"data\":" + data + "\n";
        else
            message = "\"error\":" + error + "\n";

        String output = "{\n\"success\":" + success + ",\n" + message + "}\n";
        System.out.print(output);
    }

    public ObjectNode getWeeklyScheduleObjectNode(ArrayNode schedule){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode weeklySchedule = mapper.createObjectNode();
        weeklySchedule.set("weeklySchedule",schedule);
        return weeklySchedule;
    }
    
    public ArrayNode getOfferingJSONArray(){
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

        printResult(true,"offering with code " + code  +" added successfully!","");
    }

    public void addStudent(Student newStudent){
        String id = newStudent.getStudentId();
        students.put(id, newStudent);
        printResult(true, "student with id " + id + " added successfully!", "");

    }

    public void getAllOfferings(String currStudentCode){
        try{
            findStudent(currStudentCode);
            ObjectMapper mapper = new ObjectMapper();
            printResult(true,getOfferingJSONArray(),"");
        }catch(StudentNotFound exp){
            printResult(false,"",exp.toString());
        }

    }

    public void getOffering(String currStudentCode,String offeringCode){
        try{
            findStudent(currStudentCode);
            Offering offering = findOffering(offeringCode);
            ObjectMapper mapper = new ObjectMapper();
            printResult(true,mapper.valueToTree(offering),"");

        }catch(StudentNotFound | OfferingNotFound exp){
            printResult(false,"",exp.toString());
        }
    }

    public void addtoWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.addOffering(findOffering(offeringCode));
            printResult(true,"offering with code "+ offeringCode +
                    " added to weekly schedule successfully!", "");
        }catch(StudentNotFound | OfferingNotFound exp){
            printResult(false,"",exp.toString());
        }
    }

    public void removeFromWeeklySched(String studentCode, String offeringCode){
        try{
            Student student = findStudent(studentCode);
            student.removeOffering(findOffering(offeringCode));
            printResult(true,"offering with code " + offeringCode +
                    " removed from weekly schedule successfully!","");
        }catch(StudentNotFound | OfferingNotFound exp){
            printResult(false,"",exp.toString());
        }
    }

    public void getWeeklySched(String studentCode){
        try{
            ArrayNode schedule = findStudent(studentCode).getSchedule();
            //String mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedule);
            printResult(true, getWeeklyScheduleObjectNode(schedule),"");
        }catch(StudentNotFound exp){
            printResult(false,"",exp.toString());
        }

    }

    public void finalize(String studentCode){
        try{
            Student student = findStudent(studentCode);
            student.finalizeSelection();
            printResult(true,"weekly schedule finalized successfully!","");
        }catch(Exception exp){
            printResult(false,"",exp.toString());

        }
    }

}