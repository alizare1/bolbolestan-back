package com.marshmellow.bolbolestan.JobScheduler.Job;

import com.marshmellow.bolbolestan.model.Offering;
import com.marshmellow.bolbolestan.repository.OfferingRepository;
import com.marshmellow.bolbolestan.repository.StudentRepository;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;

public class QueueJob implements Runnable {
    @Override
    public void run() {
        try {
            OfferingRepository repo = OfferingRepository.getInstance();
            StudentRepository sRepo = StudentRepository.getInstance();
            ArrayList<Offering> courses = repo.getAllOfferings();
            for (Offering course : courses) {
                ArrayList<String> waitingStudents = repo.getWaitingList(course.getCode(), course.getClassCode());
                for (String std : waitingStudents) {
                    try {
                        if (repo.getCapacity(course.getCode(), course.getClassCode())
                                > repo.getParticipantsCount(course.getCode(), course.getClassCode())) {

                            sRepo.removeCourseFromQueue(std, course.getCode(), course.getClassCode());
                            sRepo.addCourseToSubmittedCourses(std, course.getCode(), course.getClassCode());
                        } else
                            break;
                    } catch (SQLSyntaxErrorException e) {
                        break;
                    }

                }
            }
            System.out.println(java.time.LocalTime.now() + ": QueueJob Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
