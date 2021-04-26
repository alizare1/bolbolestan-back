package com.marshmellow.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.OfferingNotFound;
import com.marshmellow.Exception.StudentNotFound;
import com.marshmellow.model.CourseSelectionSystem;
import com.marshmellow.model.Offering;
import com.marshmellow.model.Student;
import com.marshmellow.model.WeeklySchedule;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @GetMapping("/{sid}")
    public Student getStudents(@PathVariable("sid") String sid) throws StudentNotFound {
        return CourseSelectionSystem.getInstance().getStudent(sid);
    }

    @GetMapping("/{sid}/plan")
    public ArrayList<Offering> getPlan(@PathVariable("sid") String sid) throws StudentNotFound {
        return CourseSelectionSystem.getInstance().getStudent(sid).getSchedule();
    }

    @GetMapping("/{sid}/schedule")
    public JsonNode getSchedule(@PathVariable("sid") String sid) throws StudentNotFound {
        return serializeSchedule(CourseSelectionSystem.getInstance().getStudent(sid).getWeeklySchedule());
    }

    @PostMapping("/{sid}/schedule")
    public JsonNode addToSchedule(@PathVariable("sid") String sid,
        @RequestBody JsonNode body) throws Exception {

        if (!body.has("code") || !body.has("group"))
            throw new Exception("Missing Parameter");

        Offering course = CourseSelectionSystem.getInstance().getCourse(body.get("code").asText(),
                body.get("group").asText());
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.addOffering(course);
        return serializeSchedule(CourseSelectionSystem.getInstance().getStudent(sid).getWeeklySchedule());
    }

    @DeleteMapping("/{sid}/schedule")
    public JsonNode resetSelection(@PathVariable("sid") String sid) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.resetSelection();
        return serializeSchedule(student.getWeeklySchedule());
    }

    @DeleteMapping("/{sid}/schedule/{code}/{group}")
    public JsonNode removeFromSchedule(@PathVariable("sid") String sid,
        @PathVariable("code") String code, @PathVariable("group") String group) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        Offering course = CourseSelectionSystem.getInstance().getCourse(code, group);
        student.removeOffering(course);
        return serializeSchedule(student.getWeeklySchedule());
    }

    @PostMapping("/{sid}/schedule/finalize")
    public JsonNode finalize(@PathVariable("sid") String sid) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.finalizeSelection();
        return serializeSchedule(student.getWeeklySchedule());
    }

    private JsonNode serializeSchedule(WeeklySchedule weeklySchedule) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode coursesArr = objectMapper.createArrayNode();
        ArrayList<Offering> submitted = weeklySchedule.getSchedule();
        ArrayList<Offering> inProgress = weeklySchedule.getInProgressCourses();
        ArrayList<Offering> queue = weeklySchedule.getInProgressQueue();
        for (Offering course : inProgress) {
            ObjectNode courseDetails = objectMapper.valueToTree(course);
            if (submitted.contains(course))
                courseDetails.put("status", "finalized");
            else
                courseDetails.put("status", "notFinalized");
            coursesArr.add(courseDetails);
        }
        for (Offering course : queue) {
            ObjectNode courseDetails = objectMapper.valueToTree(course);
            courseDetails.put("status", "queue");
            coursesArr.add(courseDetails);
        }
        for (JsonNode jsonNode : coursesArr) {
            ((ObjectNode) jsonNode).remove("classTime");
            ((ObjectNode) jsonNode).remove("examTime");
            ((ObjectNode) jsonNode).remove("capacity");
            ((ObjectNode) jsonNode).remove("prerequisites");
            ((ObjectNode) jsonNode).remove("participantsCount");
        }
        ObjectNode result = objectMapper.createObjectNode();
        result.set("courses", coursesArr);
        result.put("unitCount", weeklySchedule.getUnitCount());
        return result;
    }

//    private JsonNode serializeStudent(Student student) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode studentNode = objectMapper.valueToTree(student);
//        ArrayNode grades = objectMapper.createArrayNode();
//        for (JsonNode grade : studentNode.get("grades")) {
//            ObjectNode newGrade = objectMapper.createObjectNode();
//            newGrade.set("code", grade.get())
//        }
//        studentNode.set("grades", grades);
//        return studentNode;
//    }
}
