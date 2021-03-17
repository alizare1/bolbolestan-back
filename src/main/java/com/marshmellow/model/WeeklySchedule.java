package com.marshmellow.model;

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

    public void addOffering(Offering offering) throws  ExamTimeCollisionError, ClassTimeCollisionError {
        validateTimeCollisions(offering);
        courses.add(new Course(offering, NON_FINALIZED));
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        if (!courses.removeIf(c -> c.offering == offering))
            throw new OfferingNotFound();
    }

    private void validateUnitsCount() throws MinimumUnitsError, MaximumUnitsError {
        int units = getUnitCount();
        if (units < 12)
            throw new MinimumUnitsError();
        else if (units > 20)
            throw new MaximumUnitsError();
    }

    private void validateCapacity() throws CapacityError {
        for (Course course : courses) {
            if (course.status.equals(NON_FINALIZED)) { //TODO chera?
                if (!course.offering.hasCapacity())
                    throw new CapacityError(course.offering.getCode());
            }
        }
    }

    private void validateTimeCollisions(Offering newOffer) throws ClassTimeCollisionError, ExamTimeCollisionError {
        for (Course course : courses) {
            Offering offer2 = course.offering;
            if (newOffer.classTimeCollidesWith(offer2))
                throw new ClassTimeCollisionError(newOffer.getCode(), offer2.getCode());
            else if (newOffer.examTimeCollidesWith(offer2))
                throw new ExamTimeCollisionError(newOffer.getCode(), offer2.getCode());
        }
    }

    private void validatePrerequisites(Student student) throws PrerequisitesError{
        for(Course course : courses)
            student.validatePrerequisites(course.offering);
    }

    public void finalizeSelection(Student student)
            throws MinimumUnitsError, MaximumUnitsError, CapacityError,PrerequisitesError {

        validateUnitsCount();
        validateCapacity();
        validatePrerequisites(student);

        for (Course course : courses)
            if(course.status.equals(NON_FINALIZED)){
                 course.status = FINALIZED;
                 course.offering.addParticipant(student);
            }

     for(Offering offering : student.getRemovedCourses()) //TODO fix Cannot invoke "java.util.Collection.toArray()" because "c" is null
             offering.removeParticipant(student);

    }

    public int getUnitCount() {
        int units = 0;
        for (Course course : courses) {
            units += course.offering.getUnits();
        }
        return units;
    }

    public ArrayList<Offering> getSchedule() {
        ArrayList<Offering> stdCourses = new ArrayList<>();
        for (Course course : courses) {
            stdCourses.add(course.offering);
        }
        return stdCourses;
    }
}
