package JobScheduler.Job;

import com.marshmellow.model.Offering;
import com.marshmellow.repository.OfferingRepository;
import com.marshmellow.repository.StudentRepository;

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
                    if (repo.getCapacity(course.getCode(), course.getClassCode())
                            > repo.getParticipantsCount(course.getCode(), course.getClassCode())) {

                        sRepo.removeCourseFromQueue(std, course.getCode(), course.getClassCode());
                        sRepo.addCourseToSubmittedCourses(std, course.getCode(), course.getClassCode());
                    }
                    else
                        break;

                }
            }
            System.out.println(java.time.LocalTime.now() + ": QueueJob Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
