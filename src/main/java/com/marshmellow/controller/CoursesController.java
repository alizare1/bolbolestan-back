package com.marshmellow.controller;

import com.marshmellow.Exception.OfferingNotFound;
import com.marshmellow.Exception.StudentNotFound;
import com.marshmellow.model.CourseSelectionSystem;
import com.marshmellow.Exception.ClassTimeCollisionError;
import com.marshmellow.Exception.ExamTimeCollisionError;
import com.marshmellow.model.Offering;
import com.marshmellow.model.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/courses")
public class CoursesController {
    @GetMapping("")
    public ArrayList<Offering> getCourses(
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(value = "type", required = false, defaultValue = "") String type) {

        return CourseSelectionSystem.getInstance().getCourses(filter, type);
    }

    @GetMapping("/{code}/{group}")
    public Offering getCourse(@PathVariable("code") String code, @PathVariable("group") String group) throws OfferingNotFound {
        return CourseSelectionSystem.getInstance().getCourse(code, group);
    }
}
