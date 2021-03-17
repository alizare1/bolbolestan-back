package com.marshmellow.model;

import com.marshmellow.Exception.*;

import java.util.ArrayList;



public class WeeklySchedule {
    private final ArrayList<Offering> inProgressCourses = new ArrayList<>();
    private final ArrayList<Offering> submittedCourses = new ArrayList<>();

    public void addOffering(Offering offering) throws  ExamTimeCollisionError, ClassTimeCollisionError {
        validateTimeCollisions(offering);
        inProgressCourses.add(offering);
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        if (!inProgressCourses.removeIf(c -> c == offering))
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
        for (Offering course : getNewlyAddedCourses()) {
            if (!course.hasCapacity())
                throw new CapacityError(course.getCode());
        }
    }

    private void validateTimeCollisions(Offering newOffer) throws ClassTimeCollisionError, ExamTimeCollisionError {
        for (Offering offer2 : inProgressCourses) {
            if (newOffer.classTimeCollidesWith(offer2))
                throw new ClassTimeCollisionError(newOffer.getCode(), offer2.getCode());
            else if (newOffer.examTimeCollidesWith(offer2))
                throw new ExamTimeCollisionError(newOffer.getCode(), offer2.getCode());
        }
    }

    private void validatePrerequisites(Student student) throws PrerequisitesError{
        for(Offering course : inProgressCourses)
            student.validatePrerequisites(course);
    }

    private void validateAlreadyPassed(Student student) throws AlreadyPassedError {
        for (Offering course : getNewlyAddedCourses()) {
            if (student.hasPassed(course))
                throw new AlreadyPassedError(course.getCode());
        }

    }

    public void finalizeSelection(Student student)
            throws MinimumUnitsError, MaximumUnitsError, CapacityError, PrerequisitesError, AlreadyPassedError {

        validateUnitsCount();
        validateCapacity();
        validatePrerequisites(student);
        validateAlreadyPassed(student);

        for (Offering course : getNewlyAddedCourses()) {
            course.addParticipant(student);
            submittedCourses.add(course);
        }

        for(Offering offering : getRemovedCourses()) {
            offering.removeParticipant(student);
            submittedCourses.remove(offering);
        }

    }

    public void resetSelection() {
        inProgressCourses.clear();
        inProgressCourses.addAll(submittedCourses);
    }

    private ArrayList<Offering> getRemovedCourses() {
        ArrayList<Offering> differences = new ArrayList<Offering>(submittedCourses);
        differences.removeAll(inProgressCourses);
        return differences;
    }

    private ArrayList<Offering> getNewlyAddedCourses() {
        ArrayList<Offering> differences = new ArrayList<Offering>(inProgressCourses);
        differences.removeAll(submittedCourses);
        return differences;
    }

    public int getUnitCount() {
        int units = 0;
        for (Offering course : inProgressCourses) {
            units += course.getUnits();
        }
        return units;
    }

    public ArrayList<Offering> getSchedule() {
        return submittedCourses;
    }

    public ArrayList<Offering> getInProgressCourses() {
        return inProgressCourses;
    }
}
