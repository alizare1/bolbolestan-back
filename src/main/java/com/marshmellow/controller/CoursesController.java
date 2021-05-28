package com.marshmellow.bolbolestan.controller;

import com.marshmellow.bolbolestan.Exception.OfferingNotFound;
import com.marshmellow.bolbolestan.Exception.StudentNotFound;
import com.marshmellow.bolbolestan.Exception.ClassTimeCollisionError;
import com.marshmellow.bolbolestan.Exception.ExamTimeCollisionError;
import com.marshmellow.bolbolestan.model.Offering;
import com.marshmellow.bolbolestan.model.Student;

import com.marshmellow.bolbolestan.repository.OfferingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/courses")
public class CoursesController {
    @GetMapping("")
    public ArrayList<Offering> getCourses(
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(value = "type", required = false, defaultValue = "") String type) throws Exception {

        return OfferingRepository.getInstance().getAllOfferings();
    }

    @GetMapping("/{code}/{group}")
    public Offering getCourse(@PathVariable("code") String code, @PathVariable("group") String group) throws Exception {
        return OfferingRepository.getInstance().findById(code, group);
    }
}
