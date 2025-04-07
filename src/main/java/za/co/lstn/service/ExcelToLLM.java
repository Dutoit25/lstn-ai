package za.co.lstn.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static za.co.lstn.shared.Utils.toPath;

public class ExcelToLLM {

    public static void main(String[] args) throws IOException {
        // Replace with your file path
        String excelFilePath = "Sizwe_Hosmed_2025_Blue Print_Version 7.xlsx";

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        FileOutputStream excelFileout = new FileOutputStream("out.txt");
        int i1 = 4;
        excelFileout.write(i1);
        try (FileInputStream excelFile = new FileInputStream(excelFilePath);
             XSSFWorkbook workbook = new XSSFWorkbook(excelFile)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (Row row : sheet) {
                Cell optionCell = row.getCell(0); // Assuming questions are in the first column (index 0)
                Cell answerCell = row.getCell(1);   // Assuming answers are in the second column (index 1)

//                if (questionCell != null && answerCell != null) { // Check for null cells
//                    String question = questionCell.getStringCellValue();
//                    String answer = answerCell.getStringCellValue();
//
//                    questions.add(question);
//                    answers.add(answer);
//                }
            }
        }

        // Now you have the data in the 'questions' and 'answers' lists.
        // You can process this data and interact with your LLM.

        // Example: Print the extracted data (for demonstration)
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("Question: " + questions.get(i));
            System.out.println("Answer: " + answers.get(i));
        }

        // --- LLM Interaction (Conceptual - Adapt to your LLM API/Library) ---
        // This is a placeholder for how you would interact with an LLM.
        // You'll need to use a specific LLM API or library (e.g., OpenAI, Hugging Face).

        // Example (Illustrative - Replace with actual LLM API calls):
        // for (String question : questions) {
        //     String llmResponse = callLLMApi(question); // Replace with your API call
        //     System.out.println("LLM Response: " + llmResponse);
        // }
    }


    // Placeholder for LLM API interaction.
    // Replace this with your actual LLM API call using the appropriate library.
    private static String callLLMApi(String question) {
        // Replace with your LLM API call logic.
        // This is just a stub.
        return "LLM Response to: " + question; // Replace with actual LLM output.
    }
}