package com.marshmellow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marshmellow.Exception.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

public class WeeklyScheduleTest {
    WeeklySchedule weeklySchedule;
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        weeklySchedule = new WeeklySchedule();
        objectMapper = new ObjectMapper();
    }

    @Test(expected = ClassTimeCollisionError.class)
    public void classTimeCollisionTest()
            throws MinimumUnitsError, MaximumUnitsError, CapacityError,
            ClassTimeCollisionError, IOException, ExamTimeCollisionError {

        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/collidingTimes.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        weeklySchedule.finalizeSelection(null);
    }

    @Test(expected = ExamTimeCollisionError.class)
    public void examTimeCollisionTest()
            throws IOException, MinimumUnitsError, ClassTimeCollisionError,
            MaximumUnitsError, CapacityError, ExamTimeCollisionError {
        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/collidingExams.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        weeklySchedule.finalizeSelection(null);
    }

    @Test(expected = MaximumUnitsError.class)
    public void maxUnitTest()
            throws IOException, MinimumUnitsError, ClassTimeCollisionError,
            MaximumUnitsError, CapacityError, ExamTimeCollisionError {
        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/maxUnit.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        weeklySchedule.finalizeSelection(null);
    }

    @Test(expected = MinimumUnitsError.class)
    public void minUnitTest()
            throws IOException, MinimumUnitsError, ClassTimeCollisionError,
            MaximumUnitsError, CapacityError, ExamTimeCollisionError {
        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/minUnit.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        weeklySchedule.finalizeSelection(null);
    }

    @Test
    public void scheduleArrayTest() throws IOException, MinimumUnitsError, ClassTimeCollisionError,
            MaximumUnitsError, CapacityError, ExamTimeCollisionError, OfferingNotFound {

        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/okOfferings.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        ArrayNode nonFinalArr = weeklySchedule.getScheduleJSONArray();
        weeklySchedule.finalizeSelection(new Student());
        ArrayNode finalizedArr = weeklySchedule.getScheduleJSONArray();
        assertEquals(WeeklySchedule.NON_FINALIZED, nonFinalArr.get(0).get(WeeklySchedule.STATUS).asText());
        assertEquals(WeeklySchedule.FINALIZED, finalizedArr.get(0).get(WeeklySchedule.STATUS).asText());
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalizedArr));
    }

    @Test(expected = OfferingNotFound.class)
    public void removeOfferingTest() throws OfferingNotFound, IOException {
        Offering[] offerings = objectMapper.readValue(new File("src/test/resources/okOfferings.json"),
                Offering[].class);
        for (Offering offering : offerings) {
            weeklySchedule.addOffering(offering);
        }
        weeklySchedule.removeOffering(offerings[1]);
        assertEquals(weeklySchedule.getScheduleJSONArray().size(), 2);
        weeklySchedule.removeOffering(offerings[1]);
    }

    @Test(expected = CapacityError.class)
    public void capacityErrorTest() throws IOException, MinimumUnitsError, ClassTimeCollisionError, MaximumUnitsError,
            CapacityError, ExamTimeCollisionError {

        Offering offering = objectMapper.readValue(new File("src/test/resources/capacityErrorOffer.json"),
                Offering.class);
        offering.addParticipant(new Student());
        weeklySchedule.addOffering(offering);
        weeklySchedule.finalizeSelection(new Student());
    }
}
