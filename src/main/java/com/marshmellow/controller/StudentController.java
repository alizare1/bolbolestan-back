package com.marshmellow.controller;


import com.fasterxml.jackson.databind.JsonNode;
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
@RequestMapping("/students")
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
    public WeeklySchedule getSchedule(@PathVariable("sid") String sid) throws StudentNotFound {
        // TODO: submit status of courses
        return CourseSelectionSystem.getInstance().getStudent(sid).getWeeklySchedule();
    }

    @PostMapping("/{sid}/schedule")
    public WeeklySchedule addToSchedule(@PathVariable("sid") String sid,
        @RequestBody JsonNode body) throws Exception {

        if (!body.has("code") || !body.has("group"))
            throw new Exception("Missing Parameter");

        Offering course = CourseSelectionSystem.getInstance().getCourse(body.get("code").asText(),
                body.get("group").asText());
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.addOffering(course);
        return CourseSelectionSystem.getInstance().getStudent(sid).getWeeklySchedule();
    }

    @DeleteMapping("/{sid}/schedule")
    public WeeklySchedule resetSelection(@PathVariable("sid") String sid) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.resetSelection();
        return student.getWeeklySchedule();
    }

    @DeleteMapping("/{sid}/schedule/{code}/{group}")
    public WeeklySchedule removeFromSchedule(@PathVariable("sid") String sid,
        @PathVariable("code") String code, @PathVariable("group") String group) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        Offering course = CourseSelectionSystem.getInstance().getCourse(code, group);
        student.removeOffering(course);
        return student.getWeeklySchedule();
    }

    @PostMapping("/{sid}/schedule/finalize")
    public WeeklySchedule finalize(@PathVariable("sid") String sid) throws Exception {
        Student student = CourseSelectionSystem.getInstance().getStudent(sid);
        student.finalizeSelection();
        return student.getWeeklySchedule();
    }
}
