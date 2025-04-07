package za.co.lstn.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import za.co.lstn.dto.CustomerDTO;
import za.co.lstn.dto.EmailMessageDTO;
import za.co.lstn.model.Customer;
import za.co.lstn.model.CustomerRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class CustomerDataService {

     @Inject
     CustomerRepository customerRepository;

    public CustomerDTO extractCustomerData(EmailMessageDTO email) {
        String emailBody = email.getBody();

        String firstName = extractFirstName(emailBody);
        String lastName = extractLastName(emailBody);
        String emailAddress = extractEmailAddress(email.getFrom());
        String phoneNumber = extractPhoneNumber(emailBody);
        List<LocalDate> dates = extractDates(emailBody);
        String idNumber = extractIdNumber(emailBody);
        if (idNumber != null && idNumber.isEmpty()) {
            Customer customer = customerRepository.findById(idNumber);
            if (!Objects.isNull(customer)) {
                firstName = customer.getFirstName() != null ? customer.getFirstName() : "noname";
                lastName = customer.getLastName() != null ? customer.getLastName() : "unknown";
                emailAddress = customer.getEmail() != null ? customer.getEmail() : "noemail";
                phoneNumber = customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "nonumber";
                idNumber =  customer.getCustomerId();
            }
        }

        return CustomerDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(emailAddress)
                .phoneNumber(phoneNumber)
                .customerId(idNumber)
                .build();
    }

    public  String extractIdNumber(String text) {
        // Regex to match exactly 13 digits
        String regex = "\\b\\d{13}\\b";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the pattern in the given text
        Matcher matcher = pattern.matcher(text);

        // Check if a match exists and return the first match
        if (matcher.find()) {
            return matcher.group();
        }

        // Return empty string if nothing found
        return "";
    }

    private String extractFirstName(String text) {
        // Basic name extraction - improve with more robust techniques if needed
        String[] words = text.split("\\s+"); // Split by whitespace
        if (words.length > 0) {
            String name  = words[0];
            if(name.matches(".*\\d.*")) {
                return null;
            } else {
                return name;
            }

        }
        return null;
    }

    private String extractLastName(String text) {
        // Basic name extraction - improve with more robust techniques if needed
        String[] words = text.split("\\s+"); // Split by whitespace
        if (words.length > 1) {
            String name = words[1];
            if(name.matches(".*\\d.*")) {
                return null;
            } else {
                return name;
            }
        }
        return null;
    }

    private String extractEmailAddress(String text) {
        // Use a regular expression for email extraction
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String extractPhoneNumber(String text) {
        // Use a regular expression for phone number extraction (adapt to your expected format)
        Pattern pattern = Pattern.compile("\\b\\d{10}\\b"); // Example: 10-digit phone number
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private List<LocalDate> extractDates(String text) {
        List<LocalDate> dates = new ArrayList<>();
        List<DateTimeFormatter> formatters = new ArrayList<>(List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),  // e.g., January 1, 2024
                DateTimeFormatter.ofPattern("d MMMM yyyy")
        )); // Add more formatters as needed



        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(text, formatter);
                dates.add(date);
                return dates;
            } catch (DateTimeParseException e) {

            }
        }



        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}|\\d{2}/\\d{2}/\\d{4}|\\d{1,2}\\s+[a-zA-Z]+\\s+\\d{4})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {


            for (DateTimeFormatter formatter : formatters) {
                try {
                    LocalDate date = LocalDate.parse(matcher.group(), formatter);
                    dates.add(date);
                } catch (DateTimeParseException e) {

                }
            }

        }

        return dates;
    }


}
