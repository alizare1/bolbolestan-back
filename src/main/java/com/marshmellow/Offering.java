package com.marshmellow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

class ExamTime {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date end;

    public boolean collides(ExamTime other) {
        return this.start.compareTo(other.end) < 0 && this.end.compareTo(other.start) > 0;
    }

    @Override
    public String toString() {
        return "ExamTime{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
class ClassTime {
    public ArrayList<String> days;
    public String time;

    private Date getDate(String str) {
        DateFormat format1 = new SimpleDateFormat("HH:mm");
        DateFormat format2 = new SimpleDateFormat("HH");
        try {
            if (str.contains(":")) {
                return format1.parse(str);
            }
            return format2.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean collides(ClassTime other) {
        if (Collections.disjoint(days, other.days))
            return false;
        String[] thisTime = time.split("-", 2);
        String[] otherTime = other.time.split("-", 2);

        return getDate(thisTime[0]).compareTo(getDate(otherTime[1])) < 0
                && getDate(thisTime[1]).compareTo(getDate(otherTime[0])) > 0;
    }

    @Override
    public String toString() {
        return "ClassTime{" +
                "days=" + days +
                ", time='" + time + '\'' +
                '}';
    }
}

public class Offering {
    private String code;
    private String name;
    private String instructor;
    private int units;
    private ClassTime classTime;
    private ExamTime examTime;
    private int capacity;
    private ArrayList<String> prerequisites;
    @JsonIgnore
    private final ArrayList<Student> participants = new ArrayList<>();

    public boolean hasCapacity() {
        return participants.size() < capacity;
    }

    public boolean classTimeCollidesWith(Offering other) {
        return this.getClassTime().collides(other.getClassTime());
    }

    public boolean examTimeCollidesWith(Offering other) {
        return this.getExamTime().collides(other.getExamTime());
    }

    public int getUnits() {
        return units;
    }

    public void addParticipant(Student student) {
        participants.add(student);
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public ClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(ClassTime classTime) {
        this.classTime = classTime;
    }

    public ExamTime getExamTime() {
        return examTime;
    }

    public void setExamTime(ExamTime examTime) {
        this.examTime = examTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(ArrayList<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Override
    public String toString() {
        return "Offering{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                ", units=" + units +
                ", classTime=" + classTime +
                ", examTime=" + examTime +
                ", participants=" + participants +
                ", capacity=" + capacity +
                ", prerequisites=" + prerequisites +
                '}';
    }
}
