package za.co.lstn.service;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import jakarta.enterprise.context.ApplicationScoped;
import za.co.lstn.enums.Sentiment;


import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

@ApplicationScoped
public class SentimentAnalysisService {

    private static final String OPENAI_API_KEY = "A";  // Replace with your actual OpenAI API key


    static ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .timeout(ofSeconds(60))
            .build();

    interface SentimentAnalyzer {

        @UserMessage("Analyze sentiment of {{it}}")
        Sentiment analyzeSentimentOf(String text);

        @UserMessage("Does {{it}} have a positive sentiment?")
        boolean isPositive(String text);
    }

    public Sentiment anlyzeEmailSentiment (String message) {
        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);
        Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf(message);
        System.out.println(sentiment);
        return sentiment;
    }

}