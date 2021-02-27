package com.marshmellow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.*;

import java.util.ArrayList;


class Course {
    public Course(Offering offer, String st) {
        offering = offer;
        status = st;
    }
    public Offering offering;
    public String status;
}

public class WeeklySchedule {
    public static final String FINALIZED = "finalized";
    public static final String NON_FINALIZED = "non-finalized";
    public static final String STATUS = "status";

    private final ArrayList<Course> courses = new ArrayList<>();

    public void addOffering(Offering offering) {
        courses.add(new Course(offering, NON_FINALIZED));
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        if (!courses.removeIf(c -> c.offering == offering))
            throw new OfferingNotFound();
    }

    private void validateUnitsCount() throws MinimumUnitsError, MaximumUnitsError {
        int units = 0;
        for (Course course : courses) {
            units += course.offering.getUnits();
        }
        if (units < 12)
            throw new MinimumUnitsError();
        else if (units > 20)
            throw new MaximumUnitsError();
    }

    private void validateCapacity() throws CapacityError {
        for (Course course : courses) {
            if (course.status.equals(NON_FINALIZED)) {
                if (!course.offering.hasCapacity())
                    throw new CapacityError(course.offering.getCode());
            }
        }
    }

    private void validateTimeCollisions() throws ClassTimeCollisionError, ExamTimeCollisionError {
        for (int i = 0; i < courses.size() - 1; i++) {
            for (int j = i + 1; j < courses.size(); j++) {
                Offering offer1 = courses.get(i).offering;
                Offering offer2 = courses.get(j).offering;
                if (offer1.classTimeCollidesWith(offer2))
                    throw new ClassTimeCollisionError(offer1.getCode(), offer2.getCode());
                else if (offer1.examTimeCollidesWith(offer2))
                    throw new ExamTimeCollisionError(offer1.getCode(), offer2.getCode());
            }
        }
    }

    public void finalizeSelection(Student student)
            throws MinimumUnitsError, MaximumUnitsError, CapacityError, ClassTimeCollisionError, ExamTimeCollisionError {

        validateUnitsCount();
        validateTimeCollisions();
        validateCapacity();
        for (Course course : courses) {
            course.status = FINALIZED;
            course.offering.addParticipant(student);
        }
    }

    public ArrayNode getScheduleJSONArray() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode coursesArr = objectMapper.createArrayNode();
        for (Course course : courses) {
            ObjectNode courseDetails = objectMapper.valueToTree(course.offering);
            courseDetails.remove("units");
            courseDetails.remove("prerequisites");
            courseDetails.put(STATUS, course.status);
            coursesArr.add(courseDetails);
        }
        return coursesArr;
    }
}
