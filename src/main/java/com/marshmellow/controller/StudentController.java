package com.marshmellow.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.StudentNotFound;
import com.marshmellow.model.Offering;
import com.marshmellow.model.Student;
import com.marshmellow.repository.OfferingRepository;
import com.marshmellow.repository.StudentRepository;
import com.marshmellow.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @GetMapping("/{sid}")
    public Student getStudents(@PathVariable("sid") String sid) throws StudentNotFound, Exception {
        Student student = StudentRepository.getInstance().findById(sid);
        if (student == null)
            throw new StudentNotFound();
        return student;
    }

    @GetMapping("/{sid}/plan")
    public ArrayList<Offering> getPlan(@PathVariable("sid") String sid) throws StudentNotFound, Exception {
        ArrayList<Offering> courses = new ArrayList<>();
        for (String[] c : StudentRepository.getInstance().getSubmittedCourses(sid)) {
            courses.add(OfferingRepository.getInstance().findById(c[0], c[1]));
        }
        return courses;
    }

    @GetMapping("/{sid}/schedule")
    public JsonNode getSchedule(@PathVariable("sid") String sid) throws StudentNotFound, Exception {
        return getScheduleFromRepo(sid);
    }

    @PostMapping("/{sid}/schedule")
    public JsonNode addToSchedule(@PathVariable("sid") String sid,
        @RequestBody JsonNode body) throws Exception {

        if (!body.has("code") || !body.has("group"))
            throw new Exception("Missing Parameter");

        StudentService.addToSchedule(sid, body.get("code").asText(), body.get("group").asText());
        return getScheduleFromRepo(sid);
    }

    @DeleteMapping("/{sid}/schedule")
    public JsonNode resetSelection(@PathVariable("sid") String sid) throws Exception {
        StudentRepository repo = StudentRepository.getInstance();
        repo.clearInProgCourses(sid);
        repo.clearInProgQueue(sid);
        for (String[] c : repo.getSubmittedCourses(sid)) {
            repo.addCourseToInProgCourses(sid, c[0], c[1]);
        }
        for (String[] c : repo.getQueueCourses(sid)) {
            repo.addCourseToInProgQueue(sid, c[0], c[1]);
        }
        return getScheduleFromRepo(sid);
    }

    @DeleteMapping("/{sid}/schedule/{code}/{group}")
    public JsonNode removeFromSchedule(@PathVariable("sid") String sid,
        @PathVariable("code") String code, @PathVariable("group") String group) throws Exception {
        StudentRepository repo = StudentRepository.getInstance();
        repo.removeCourseFromInProg(sid, code, group);
        repo.removeCourseFromInProgQueue(sid, code, group);
        return getScheduleFromRepo(sid);
    }

    @PostMapping("/{sid}/schedule/finalize")
    public JsonNode finalize(@PathVariable("sid") String sid) throws Exception {
        StudentService.finalizeSelection(sid);
        return getScheduleFromRepo(sid);
    }

    private JsonNode getScheduleFromRepo(String sid) throws Exception {
        ArrayList<Offering> submitted = StudentService.getOfferingArrayList(
                StudentRepository.getInstance().getSubmittedCourses(sid));

        ArrayList<Offering> inProgress = StudentService.getOfferingArrayList(
                StudentRepository.getInstance().getInProgCourses(sid));

        ArrayList<Offering> queue = StudentService.getOfferingArrayList(
                StudentRepository.getInstance().getInProgQueueCourses(sid));

        int unitCount = StudentRepository.getInstance().getInProgUnitCount(sid);

        return serializeSchedule(unitCount, submitted, inProgress, queue);
    }

    private JsonNode serializeSchedule(int unitCount, ArrayList<Offering> submitted, ArrayList<Offering> inProgress, ArrayList<Offering> queue) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode coursesArr = objectMapper.createArrayNode();
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
        result.put("unitCount", unitCount);
        return result;
    }
}
