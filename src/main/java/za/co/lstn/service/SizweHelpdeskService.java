package za.co.lstn.service;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4;
import static java.util.stream.Collectors.joining;


import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;

import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;

import io.quarkus.logging.Log;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import redis.clients.jedis.Jedis;
import za.co.lstn.dto.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import za.co.lstn.prompt.SimpelPromptTemplate;

@ApplicationScoped
public class SizweHelpdeskService {

    @Inject
    SimpelPromptTemplate simpelPromptTemplate;

    @Inject
    EmailAnalyzer emailAnalyzer;

    @Inject
    PromptEngineeringService promptEngineeringService;

    @Inject
    DocumentEmbeddingService documentEmbeddingService;

    /**
     * The embedding store (the database).
     * The bean is provided by the quarkus-langchain4j-redis extension.
     */

    private static Jedis jedis;
    private static final String REDIS_HOST = "localhost"; // Replace with your Redis host
    private static final int REDIS_PORT = 6379;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private static final String OPENAI_API_KEY = "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";  // Replace with your actual OpenAI API key
    private static final String DOCUMENT_PATH = "data/Sizwe-Hosmed-Full-Member-Guide-Nov-2023.pdf";
    private static final int CLOUD_REDIS_PORT = 14956;
    private static final String CLOUD_REDIS_HOST = "redis-14956.c283.us-east-1-4.ec2.redns.redis-cloud.com";

    public static void main(String[] args) {
        importDocuments();
    }

    public static void importDocuments() {
        try {
        DocumentEmbeddingService documentEmbeddingService = new DocumentEmbeddingService();
        documentEmbeddingService.importDocuments();
//        documentEmbeddingService.importExcelDocument("Sizwe_Hosmed_2025_Blue Print_Version 7.xlsx");
        } catch (Exception ex) {
             ex.printStackTrace();
        }
    }

    public EmailMessageDTO convertToEmailMessage(String input) {
        Log.info("convertToEmailMessage:....");
        EmailMessageDTO emailMessageDTO = new EmailMessageDTO();

        try {
            // Initialize `from`, `subject`, and `body` variables
            String from = "";
            String subject = "";
            String body = input;

            // Regex patterns to attempt extracting 'from' and 'subject'
            Pattern fromPattern = Pattern.compile("(?i)from:(.*?)(\\r?\\n|$)");
            Pattern subjectPattern = Pattern.compile("(?i)subject:(.*?)(\\r?\\n|$)");

            Matcher fromMatcher = fromPattern.matcher(input);
            Matcher subjectMatcher = subjectPattern.matcher(input);

            // Extract 'from' if found
            if (fromMatcher.find()) {
                from = fromMatcher.group(1).trim();
            }

            // Extract 'subject' if found
            if (subjectMatcher.find()) {
                subject = subjectMatcher.group(1).trim();
            }

            // Remove extracted parts from body
            input = input.replaceFirst(fromPattern.pattern(), "");
            input = input.replaceFirst(subjectPattern.pattern(), "");
            body = input.trim();

            // Set the extracted or default values into EmailMessageDTO
            emailMessageDTO.setFrom(from != null ? from : "");
            emailMessageDTO.setSubject(subject != null ? subject : "");
            emailMessageDTO.setBody(body != null ? body : "");

        } catch (Exception e) {
            // Safeguard and set fallback default values in case of any error
            emailMessageDTO.setFrom("");
            emailMessageDTO.setSubject("");
            emailMessageDTO.setBody(input); // Default to input as the body
        }

        return emailMessageDTO;
    }

    public String processEmail(EmailMessageDTO email) {

        ExtractEmailContentDTO extractEmailContentDTO = emailAnalyzer.analyzeEmail(email);
        //get RAG Description
        UserPromptRequest userPromptRequest = new UserPromptRequest();
        userPromptRequest.setPrompt(email.getBody());
        AnswerDTO answerDTO = getRedisPrompt(userPromptRequest.getPrompt());
        //update extractEmailContentDTO
        extractEmailContentDTO.setRagAnswerDTO(answerDTO);
        promptEngineeringService.selectBestPrompt(extractEmailContentDTO);
        simpelPromptTemplate.createPrompt(extractEmailContentDTO);
        return extractEmailContentDTO.getEmailReponse();
    }


    public AnswerDTO getRedisPrompt(String question) {
        UserPromptRequest userPrompt = new UserPromptRequest();
        userPrompt.setPrompt(question);

        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host(CLOUD_REDIS_HOST)
                .port(CLOUD_REDIS_PORT)
                .dimension(384)
                .user("default")
                .password("npGGTMCQjvxUizqBp4V3u6uFmdzPTogU")
                .indexName("SIZWE2025")
                .build();

        // Embed the question
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        int maxResults = 3;
        double minScore = 0.7;
       List<EmbeddingMatch<TextSegment>> relevantEmbeddings
                = embeddingStore.findRelevant(questionEmbedding, maxResults, minScore);

        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question to the best of your ability:\n"
                        + "\n"
                        + "Question:\n"
                        + "{{question}}\n"
                        + "\n"
                        + "Base your answer on the following information:\n"
                        + "{{information}}");

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);

        Prompt prompt = promptTemplate.apply(variables);

        // Send the prompt to the OpenAI chat model
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4)
                .timeout(Duration.ofSeconds(60))
                .build();

        AiMessage aiMessage = chatModel.generate(prompt.toUserMessage()).content();

        // See an answer from the model
        String answer = aiMessage.text();
        System.out.println();
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println( question);

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println(answer);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setQuestion(question);
        answerDTO.setAnswer(answer);

        return answerDTO;
    }

    public String updateRagModelFromUserEnhancement(String ragMessage) {
        try {
            return documentEmbeddingService.updateRagFromUserSuggestion(ragMessage);
        } catch (Exception e) {
            System.err.println("Error importing message: " + e.getMessage());
            return e.getMessage();
        }
    }


}


