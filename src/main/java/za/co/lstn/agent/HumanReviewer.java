package za.co.lstn.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class HumanReviewer {

//    private static final ObjectMapper objectMapper = new ObjectMapper()
//            .registerModule(new JavaTimeModule());

//    public static void main(String[] args) {
//        try {
//            // 1. Load the generated response from a file (replace with your actual loading logic)
//            GeneratedResponse generatedResponse = loadGeneratedResponseFromFile("generated_response.json");
//
//            // 2. Allow human review and editing
//            String editedResponse = getEditedResponseFromHuman(generatedResponse.response);
//
//            // 3. Update the response in the object
//            generatedResponse.response = editedResponse;
//            generatedResponse.lastEditedBy= "HumanReviewer";
//            generatedResponse.lastEditedAt = LocalDateTime.now();
//
//            // 4. Save the updated response to a file
//            saveEditedResponseToFile(generatedResponse, "edited_response.json");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static GeneratedResponse loadGeneratedResponseFromFile(String filePath)
//            throws IOException {
//        return objectMapper.readValue(new File(filePath), GeneratedResponse.class);
//    }
//
//    private static String getEditedResponseFromHuman(String generatedResponse) {
//        // This is a placeholder. Implement your actual human review and editing logic here.
//        // For example, you could present the response to a user interface, allow them to edit it,
//        // and return the edited string.
//        System.out.println("Generated Response:\n" + generatedResponse);
//        System.out.print("Enter your edited response: ");
//        return System.console().readLine();
//    }
//
//    private static void saveEditedResponseToFile(GeneratedResponse response, String filePath) throws IOException {
//        objectMapper.writeValue(new File(filePath), response);
//    }
//
//    // Helper class to represent the generated response
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    public static class GeneratedResponse {
//        private String response;
//        private String lastEditedBy;
//        private LocalDateTime lastEditedAt;
//
//        // Getters and Setters (omitted for brevity)
//    }
}