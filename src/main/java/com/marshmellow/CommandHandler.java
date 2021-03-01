package com.marshmellow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;

public class CommandHandler {
    // Constants
    private static final int COMMAND_INDEX = 0;
    private static final int JSON_INDEX = 1;
    private static final String ADD_OFFERING = "addOffering";
    private static final String ADD_STUDENT = "addStudent";
    private static final String GET_OFFERINGS = "getOfferings";
    private static final String GET_OFFERING = "getOffering";
    private static final String ADD_TO_SCHEDULE = "addToWeeklySchedule";
    private static final String REMOVE_FROM_SCHEDULE = "removeFromWeeklySchedule";
    private static final String GET_SCHEDULE = "getWeeklySchedule";
    private static final String FINALIZE = "finalize";

    private final CourseSelectionSystem courseSelectionSystem = new CourseSelectionSystem();

    private void printJson(JsonNode json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            String [] splitInput = input.split(" ", 2);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            if (splitInput.length > 1)
                 jsonNode = objectMapper.readTree(splitInput[JSON_INDEX]);
            switch (splitInput[COMMAND_INDEX]) {
                case ADD_OFFERING:
                    Offering newOffering = objectMapper.readValue(splitInput[JSON_INDEX], Offering.class);
                    printJson(courseSelectionSystem.addOffering(newOffering));
                    break;
                case ADD_STUDENT:
                    Student newStudent = objectMapper.readValue(splitInput[JSON_INDEX], Student.class);
                    printJson(courseSelectionSystem.addStudent(newStudent));
                    break;
                case GET_OFFERINGS:
                    printJson(courseSelectionSystem.getAllOfferings(jsonNode.get("studentId").asText()));
                    break;
                case GET_OFFERING:
                    printJson(courseSelectionSystem.getOffering(jsonNode.get("studentId").asText(),
                            jsonNode.get("code").asText()));
                    break;
                case ADD_TO_SCHEDULE:
                    printJson(courseSelectionSystem.addtoWeeklySched(jsonNode.get("studentId").asText(),
                            jsonNode.get("code").asText()));
                    break;
                case REMOVE_FROM_SCHEDULE:
                    printJson(courseSelectionSystem.removeFromWeeklySched(jsonNode.get("studentId").asText(),
                            jsonNode.get("code").asText()));
                    break;
                case GET_SCHEDULE:
                    printJson(courseSelectionSystem.getWeeklySched(jsonNode.get("studentId").asText()));
                    break;
                case FINALIZE:
                    printJson(courseSelectionSystem.finalize(jsonNode.get("studentId").asText()));
                    break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.run();
    }
}
