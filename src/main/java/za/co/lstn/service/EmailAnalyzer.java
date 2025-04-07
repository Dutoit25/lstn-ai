package za.co.lstn.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import za.co.lstn.dto.CustomerDTO;
import za.co.lstn.dto.EmailMessageDTO;
import za.co.lstn.dto.ExtractEmailContentDTO;
import za.co.lstn.enums.EmailCategory;
import za.co.lstn.enums.Sentiment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

@ApplicationScoped
public class EmailAnalyzer {

    @Inject
    CustomerDataService customerDataExtractorService;

    @Inject
    SentimentAnalysisService sentimentAnalysisService;

    private static final String OPENAI_API_KEY = "";  // Replace with your actual OpenAI API key

    static ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .timeout(ofSeconds(60))
            .build();

    public ExtractEmailContentDTO analyzeEmail(EmailMessageDTO email) {

        CustomerDTO customerDTO = customerDataExtractorService
                .extractCustomerData(email);

        EmailCategory emailCategory = categorizeEmail(email.getBody());
        System.out.println("Email category: " + emailCategory);

        Sentiment sentiment = sentimentAnalysisService.anlyzeEmailSentiment(email.getBody());

        String issue = findIssueWithRegex(email.getBody());
        System.out.println("Identified issue: " + issue);

        return ExtractEmailContentDTO.builder()
                .emailMessageDTO(email)
                .customerDTO(customerDTO)
                .sentiment(sentiment)
                .category(emailCategory)
                .build();
    }


    public EmailCategory categorizeEmail(String text) {
        String[] keywords = text.toLowerCase().split("\\s+");
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (String keyword : keywords) {
            for (EmailCategory emailCategory : EmailCategory.values()) {
                if (emailCategory.getKeywords().contains(keyword)) {
                    categoryCounts.put(emailCategory.getName(),
                            categoryCounts.getOrDefault(emailCategory.getName(), 0) + 1);
                }
            }
        }
        EmailCategory  emailCategory = EmailCategory.NO_CATEGORY;
        String maxCategory = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxCategory = entry.getKey();
                emailCategory = EmailCategory.valueOf(maxCategory);
            }
        }
        return emailCategory;
    }

    public  String analyzeSimpleSentiment(String text) {
        // Implement sentiment analysis using a Java library like VADER or a custom implementation
        // This is a simplified example
        String[] negativeKeywords = {"problem", "issue", "error", "dissatisfied", "unhappy"};
        String[] positiveKeywords = {"appreciate", "thank", "correct", "satisfied", "happy"};
        for (String keyword : negativeKeywords) {
            if (text.toUpperCase().contains(keyword)) {
                return "NEGATIVE";
            }
        }
        for (String keyword : positiveKeywords) {
            if (text.toUpperCase().contains(keyword)) {
                return "POSITIVE";
            }
        }
        return "NEUTRAL"; // Placeholder for more sophisticated sentiment analysis
    }

    public  String findIssueWithRegex(String text) {
        String[] patterns = {
                "order\\s+not\\s+received",
                "refund\\s+request",
                "technical\\s+difficulties"
        };

        for (String pattern : patterns) {
            Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = r.matcher(text);
            if (m.find()) {
                return pattern;
            }
        }
        return null;
    }
}
