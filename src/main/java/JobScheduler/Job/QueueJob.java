package JobScheduler.Job;

import com.marshmellow.model.CourseSelectionSystem;
import com.marshmellow.model.Offering;

import java.util.ArrayList;

public class QueueJob implements Runnable {
    @Override
    public void run() {
        ArrayList<Offering> courses = CourseSelectionSystem.getInstance().getCourses("");
        for (Offering course : courses) {
            course.handleQueue();
        }
        System.out.println(java.time.LocalTime.now() + ": QueueJob Done!");
    }
}
