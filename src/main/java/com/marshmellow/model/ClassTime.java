package com.marshmellow.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ClassTime {
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
