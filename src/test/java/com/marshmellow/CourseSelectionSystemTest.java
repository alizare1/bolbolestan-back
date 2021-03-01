package com.marshmellow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class CourseSelectionSystemTest {
    static ObjectMapper objectMapper = new ObjectMapper();
    CourseSelectionSystem courseSelectionSystem;
    static Offering[] offerings;
    static Student[] students;

    @BeforeClass
    public static void initialSetup() throws IOException {
        offerings = objectMapper.readValue(new File("src/test/resources/okOfferings.json"),
                Offering[].class);

        students = objectMapper.readValue(new File("src/test/resources/students.json"),
                Student[].class);
    }

    @Before
    public void setup() {
        courseSelectionSystem = new CourseSelectionSystem();
        for (Student student : students)
            courseSelectionSystem.addStudent(student);

        for (Offering offering : offerings)
            courseSelectionSystem.addOffering(offering);
    }

    @Test
    public void addStudentTest() {
        CourseSelectionSystem tempSystem = new CourseSelectionSystem();
        for (Student student : students) {
            JsonNode result = tempSystem.addStudent(student);
            assertTrue(result.get("success").asBoolean());
        }
    }

    @Test
    public void addOfferingTest() {
        CourseSelectionSystem tempSystem = new CourseSelectionSystem();
        for (Offering offering : offerings) {
            JsonNode result = tempSystem.addOffering(offering);
            assertTrue(result.get("success").asBoolean());
        }
    }

    @Test
    public void getAllOfferingsTest() {
        JsonNode result = courseSelectionSystem.getAllOfferings(students[0].getStudentId());
        assertTrue(result.get("success").asBoolean());
        assertEquals(result.get("data").size(), offerings.length);
    }

    @Test
    public void getOfferingTest() {
        JsonNode result = courseSelectionSystem.getOffering(students[0].getStudentId(), offerings[0].getCode());
        assertTrue(result.get("success").asBoolean());
        assertEquals(result.get("data").get("name").asText(), offerings[0].getName());
    }

    @Test
    public void getNonExistingOffering() {
        JsonNode result = courseSelectionSystem.getOffering(students[0].getStudentId(), "99");
        assertFalse(result.get("success").asBoolean());
    }

    @Test
    public void addToWeeklyScheduleTest() {
        JsonNode result = courseSelectionSystem.addtoWeeklySched(students[0].getStudentId(), offerings[0].getCode());
        assertTrue(result.get("success").asBoolean());
    }

    @Test
    public void getWeeklyScheduleTest() {
        courseSelectionSystem.addtoWeeklySched(students[0].getStudentId(), offerings[0].getCode());
        JsonNode result = courseSelectionSystem.getWeeklySched(students[0].getStudentId());
        assertTrue(result.get("success").asBoolean());
        assertEquals(result.get("data").get(0).get("code").asText(), offerings[0].getCode());
        assertEquals(result.get("data").get(0).get("status").asText(), "non-finalized");
    }

    @Test
    public void finalizeFailTest() {
        courseSelectionSystem.addtoWeeklySched(students[0].getStudentId(), offerings[0].getCode());
        JsonNode result = courseSelectionSystem.finalize(students[0].getStudentId());
        assertFalse(result.get("success").asBoolean());
    }

    @Test
    public void finalizeTest() {
        for (Offering offering : offerings)
            courseSelectionSystem.addtoWeeklySched(students[1].getStudentId(), offering.getCode());
        JsonNode result = courseSelectionSystem.finalize(students[1].getStudentId());
        assertTrue(result.get("success").asBoolean());
        result = courseSelectionSystem.getWeeklySched(students[1].getStudentId());
        assertEquals(result.get("data").get(0).get("status").asText(), "finalized");
    }
}
