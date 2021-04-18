package com.marshmellow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.marshmellow.Exception.*;

import java.util.ArrayList;



public class WeeklySchedule {
    private final ArrayList<Offering> inProgressCourses = new ArrayList<>();
    private final ArrayList<Offering> submittedCourses = new ArrayList<>();
    private final ArrayList<Offering> inProgressWaitingCourses = new ArrayList<>();
    private final ArrayList<Offering> waitingCourses = new ArrayList<>();

    public void addOffering(Offering offering) throws  ExamTimeCollisionError, ClassTimeCollisionError {
        validateTimeCollisions(offering);
        inProgressCourses.add(offering);
    }

    public void addToQueue(Offering offering) throws Exception {
        validateTimeCollisions(offering);
        inProgressWaitingCourses.add(offering);
    }

    public void submitFromQueue(Offering offering) {
        waitingCourses.remove(offering);
        inProgressWaitingCourses.remove(offering);
        inProgressCourses.add(offering);
        submittedCourses.add(offering);
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        if (!inProgressCourses.removeIf(c -> c == offering) && !inProgressWaitingCourses.removeIf(c -> c == offering))
            throw new OfferingNotFound();
    }

    public void removeFromQueue(Offering offering) throws OfferingNotFound {
        if (!inProgressWaitingCourses.removeIf(c -> c == offering))
            throw new OfferingNotFound();
    }

    private void validateUnitsCount() throws MinimumUnitsError, MaximumUnitsError {
        int units = getUnitCount();
        int waitingUnits = getUnits(inProgressWaitingCourses);
        if (units < 12)
            throw new MinimumUnitsError();
        else if (units + waitingUnits > 20)
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

        for(Offering course : waitingCourses)
            student.validatePrerequisites(course);
    }

    private void validateAlreadyPassed(Student student) throws AlreadyPassedError {
        for (Offering course : getNewlyAddedCourses()) {
            if (student.hasPassed(course))
                throw new AlreadyPassedError(course.getCode());
        }

        for (Offering course : getNewQueues()) {
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

        for (Offering offering : getRemovedCourses()) {
            offering.removeParticipant(student);
            submittedCourses.remove(offering);
        }

        for (Offering course : getNewQueues()) {
            course.addToQueue(student);
            waitingCourses.add(course);
        }

        for (Offering course : getRemovedQueues()) {
            course.removeFromQueue(student);
            waitingCourses.remove(course);
        }

    }

    public void resetSelection() {
        inProgressCourses.clear();
        inProgressWaitingCourses.clear();
        inProgressCourses.addAll(submittedCourses);
        inProgressWaitingCourses.addAll(waitingCourses);
    }

    private ArrayList<Offering> getRemovedCourses() {
        return getDiffOfferings(submittedCourses, inProgressCourses);
    }

    private ArrayList<Offering> getNewlyAddedCourses() {
        return getDiffOfferings(inProgressCourses, submittedCourses);
    }

    private ArrayList<Offering> getNewQueues() {
        return getDiffOfferings(inProgressWaitingCourses, waitingCourses);
    }

    private ArrayList<Offering> getRemovedQueues() {
        return getDiffOfferings(waitingCourses, inProgressWaitingCourses);
    }

    private ArrayList<Offering> getDiffOfferings(ArrayList<Offering> new_, ArrayList<Offering> old) {
        ArrayList<Offering> differences = new ArrayList<Offering>(new_);
        differences.removeAll(old);
        return differences;
    }

    public int getUnitCount() {
        return getUnits(inProgressCourses);
    }

    private int getUnits(ArrayList<Offering> list) {
        int units = 0;
        for (Offering course : list) {
            units += course.getUnits();
        }
        return units;
    }

    @JsonIgnore
    public ArrayList<Offering> getSchedule() {
        return submittedCourses;
    }

    public ArrayList<Offering> getInProgressCourses() {
        return inProgressCourses;
    }

    public ArrayList<Offering> getInProgressQueue() {
        return inProgressWaitingCourses;
    }
}
