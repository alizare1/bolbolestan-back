package com.marshmellow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marshmellow.Exception.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private final WeeklySchedule schedule = new WeeklySchedule();

    public void addOffering(Offering offering) {
        schedule.addOffering(offering);
    }

    public void removeOffering(Offering offering) throws OfferingNotFound {
        schedule.removeOffering(offering);
    }

    public void finalizeSelection() throws MinimumUnitsError, ClassTimeCollisionError, MaximumUnitsError,
            CapacityError, ExamTimeCollisionError {
        schedule.finalizeSelection(this);
    }

    public ArrayNode getSchedule() {
        return schedule.getScheduleJSONArray();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(String enteredAt) {
        this.enteredAt = enteredAt;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", enteredAt='" + enteredAt + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
