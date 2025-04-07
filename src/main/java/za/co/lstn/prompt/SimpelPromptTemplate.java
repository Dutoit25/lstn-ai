package za.co.lstn.prompt;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import za.co.lstn.dto.ExtractEmailContentDTO;
import za.co.lstn.enums.Sentiment;

import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

@ApplicationScoped
public class SimpelPromptTemplate implements MasterPromptTemplate{

    private static final String OPENAI_API_KEY = "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";  // Replace with your actual OpenAI API key

    public ExtractEmailContentDTO createPrompt(ExtractEmailContentDTO extractEmailContentDTO) {
        return createSentimentPrompt(extractEmailContentDTO);
    }


    public ExtractEmailContentDTO createSentimentPrompt(ExtractEmailContentDTO extractEmailContentDTO) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .timeout(ofSeconds(60))
                .build();

        Sentiment sentiment = extractEmailContentDTO.getSentiment();
        String response = "";

        // Define sentiment-specific templates in a reusable manner
        Map<Sentiment, String> templates = Map.of(
                Sentiment.NEGATIVE,
                "A {{customer}} was complaining about our service. Here is the details of the customer's complaint: {{emailBody}}. " +
                        "Can you respond to this unhappy customer by assuring them we're working on their complaint? This was their original claim: {{emailBody}}. " +
                        "We also find additional information {{information}} in our documents. Can you compose a response for" +
                        "customer that includes this additional information? You are a Frederik from Sizwe Heldesk, Your Email address is frederik@noemail.co.za." +
                        "Your Contact Information is: 082 123 4567" ,

                Sentiment.POSITIVE,
                "A {{customer}} was complimenting our service. Here are the details of the compliment: {{emailBody}}. " +
                        "Can you respond to this happy customer with an appreciation email? This was their original claim: {{emailBody}}" +
                        "We also find additional information {{information}} in our documents. Can you compose a response for" +
                        "customer that includes this additional information? You are a Frederik from Sizwe Heldesk, Your Email address is frederik@noemail.co.za." +
                        "Your Contact Information is: 082 123 4567",

                Sentiment.NEUTRAL,
                "A {{customer}} shared feedback about our service. Here are the details of the feedback: {{emailBody}}. " +
                        "Can you compose a courteous and neutral response for the customer? This was their original feedback: {{emailBody}}" +
                        "We also find additional information {{information}} in our documents. Can you compose a response for" +
                        "customer that includes this additional information? You are a Frederik from Sizwe Heldesk, Your Email address is frederik@noemail.co.za." +
                        "Your Contact Information is: 082 123 4567"
        );


        // Get the appropriate template based on sentiment
        String template = templates.get(sentiment);
        if (template != null) {
            PromptTemplate promptTemplate = PromptTemplate.from(template);
            Map<String, Object> variables = new HashMap<>();
            if (extractEmailContentDTO.getCustomerDTO().getFirstName() != null) {
                variables.put("customer", extractEmailContentDTO.getCustomerDTO().getFirstName()); // Replace with actual dynamic values
            }else {
                variables.put("customer", "Du Toit"); // Replace with actual dynamic values
            }

            variables.put("emailBody", extractEmailContentDTO.getEmailMessageDTO().getBody());
            variables.put("information", extractEmailContentDTO.getRagAnswerDTO().getAnswer());
            // Apply the template with variables
            Prompt prompt = promptTemplate.apply(variables);
            response = model.generate(prompt.text());
            System.out.println(response);
        }

        // Set the response
        extractEmailContentDTO.setEmailReponse(response);
        return extractEmailContentDTO;
    }
}
