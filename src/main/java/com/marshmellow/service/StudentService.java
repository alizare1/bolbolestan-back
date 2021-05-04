package com.marshmellow.service;

import com.marshmellow.Exception.*;
import com.marshmellow.model.Offering;
import com.marshmellow.repository.OfferingRepository;
import com.marshmellow.repository.StudentRepository;

import java.util.ArrayList;

public class StudentService {
    public static void addToSchedule(String sid, String code, String groupCode) throws Exception {
        OfferingRepository offeringRepo = OfferingRepository.getInstance();
        StudentRepository studentRepo = StudentRepository.getInstance();
        Offering course = offeringRepo.findById(code, groupCode);
        validateTimeCollisions(sid, course);
        if (offeringRepo.getCapacity(course.getCode(), course.getClassCode()) > offeringRepo.getParticipantsCount(course.getCode(), course.getClassCode()))
            studentRepo.addCourseToInProgCourses(sid, code, groupCode);
        else
            studentRepo.addCourseToInProgQueue(sid, code, groupCode);

    }

    public static void finalizeSelection(String sid) throws Exception {
        StudentRepository repo = StudentRepository.getInstance();
//        validateUnitsCount(sid);
        validateCapacity(sid);
        validatePrerequisites(sid);
        validateAlreadyPassed(sid);

        repo.clearSubmittedCourses(sid);
        for (String[] c : repo.getInProgCourses(sid)) {
            repo.addCourseToSubmittedCourses(sid, c[0], c[1]);
        }

        repo.clearQueue(sid);
        for (String[] c : repo.getInProgQueueCourses(sid)) {
            repo.addCourseToQueue(sid, c[0], c[1]);
        }

    }

    public static ArrayList<Offering> getOfferingArrayList(ArrayList<String[]> codes) throws Exception {
        ArrayList<Offering> offerings = new ArrayList<>();
        for (String[] c : codes) {
            offerings.add(OfferingRepository.getInstance().findById(c[0], c[1]));
        }
        return offerings;
    }

    private static void validateTimeCollisions(String sid, Offering newOffer) throws Exception {
        StudentRepository sRepo = StudentRepository.getInstance();
        ArrayList<Offering> inProgressCourses = getOfferingArrayList(sRepo.getInProgCourses(sid));
        ArrayList<Offering> inProgressWaitingCourses = getOfferingArrayList(sRepo.getInProgQueueCourses(sid));

        for (Offering offer2 : inProgressCourses) {
            if (newOffer.classTimeCollidesWith(offer2))
                throw new ClassTimeCollisionError(newOffer.getCode(), offer2.getCode());
            else if (newOffer.examTimeCollidesWith(offer2))
                throw new ExamTimeCollisionError(newOffer.getCode(), offer2.getCode());
        }

        for (Offering offer2 : inProgressWaitingCourses) {
            if (newOffer.classTimeCollidesWith(offer2))
                throw new ClassTimeCollisionError(newOffer.getCode(), offer2.getCode());
            else if (newOffer.examTimeCollidesWith(offer2))
                throw new ExamTimeCollisionError(newOffer.getCode(), offer2.getCode());
        }
    }

    private static void validateUnitsCount(String sid) throws Exception {
        StudentRepository sRepo = StudentRepository.getInstance();
        int units = sRepo.getInProgUnitCount(sid);
        int waitingUnits = sRepo.getInProgWaitUnitCount(sid);
        if (units < 12)
            throw new MinimumUnitsError();
        else if (units + waitingUnits > 20)
            throw new MaximumUnitsError();
    }

    private static void validateCapacity(String sid) throws Exception {
        for (String[] c : StudentRepository.getInstance().getNewlyAddedCourses(sid)) {
            int cap = OfferingRepository.getInstance().getCapacity(c[0], c[1]);
            int stdCount = OfferingRepository.getInstance().getCapacity(c[0], c[1]);
            if (cap <= stdCount)
                throw new CapacityError(c[0]);
        }
    }

    private static void validatePrerequisites(String sid) throws Exception {
        ArrayList<String> passedCourses = StudentRepository.getInstance().getPassedCourses(sid);
        for(String[] c : StudentRepository.getInstance().getNewlyAddedCourses(sid)) {
            for (String pre: OfferingRepository.getInstance().getPrerequisites(c[0]))
                if (!passedCourses.contains(pre))
                    throw new PrerequisitesError(pre, c[0]);
        }
        for(String[] c : StudentRepository.getInstance().getInProgQueueCourses(sid)) {
            for (String pre: OfferingRepository.getInstance().getPrerequisites(c[0]))
                if (!passedCourses.contains(pre))
                    throw new PrerequisitesError(pre, c[0]);
        }
    }

    private static void validateAlreadyPassed(String sid) throws Exception {
        ArrayList<String> passedCourses = StudentRepository.getInstance().getPassedCourses(sid);
        for (String[] c : StudentRepository.getInstance().getInProgCourses(sid)) {
            if (passedCourses.contains(c[0]))
                throw new AlreadyPassedError(c[0]);
        }
        for (String[] c : StudentRepository.getInstance().getInProgQueueCourses(sid)) {
            if (passedCourses.contains(c[0]))
                throw new AlreadyPassedError(c[0]);
        }
    }
}
