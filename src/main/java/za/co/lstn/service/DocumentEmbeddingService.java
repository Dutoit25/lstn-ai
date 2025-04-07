package za.co.lstn.service;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4;
import static java.util.stream.Collectors.joining;
import static za.co.lstn.service.ImprovedDocumentSplitter.splitDocument;
import static za.co.lstn.shared.Utils.OPENAI_API_KEY;
import static za.co.lstn.shared.Utils.toPath;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.DefaultDocument;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.logging.Log;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import redis.clients.jedis.Jedis;
import za.co.lstn.dto.AnswerDTO;
import za.co.lstn.dto.DocumentImportRequestDTO;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApplicationScoped
public class DocumentEmbeddingService {


    private static final String OPENAI_API_KEY = "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";  // Replace with your actual OpenAI API key
    private static final String DOCUMENT_PATH = "data/Sizwe-Hosmed-Full-Member-Guide-Nov-2023.pdf";
    private static final int CLOUD_REDIS_PORT = 14956;
    private static final String CLOUD_REDIS_HOST = "redis-14956.c283.us-east-1-4.ec2.redns.redis-cloud.com";
    int maxChunkSize = 512;
    int maxOverlap = 50; // Roughly 10% of chunk size

    /**
     * Import a PDF document into Redis for RAG (Retrieval-Augmented Generation).
     * This includes parsing, splitting into smaller segments, generating embeddings, and storing in Redis.
     *
     * @param documentFilePath - Path of the PDF document to be imported
     */
    /**
     * Import a PDF document into Redis for RAG (Retrieval-Augmented Generation).
     * This includes parsing, splitting into smaller segments, generating embeddings, and storing in Redis.
     */
    public List<TextSegment> loadAndSaveDocuments(DocumentImportRequestDTO documentImportRequestDTO) {
        List<TextSegment> segments = null;
        try {

            switch (documentImportRequestDTO.getSplitterLevel()) {
                case 1: {
                    Log.info("Split Level 1");
                    break;
                }
                case 2:
//                  Recursive Character Text Splitting -
//                  Recursive chunking based on a list of separators
                {
                    Log.info("Split Level 2: Recursive Character Text Splitting");
                    segments = splitLevel2(documentImportRequestDTO);
                    break;
                }
                case 3:
//                    Document Specific Splitting - Various chunking methods
//                    for different document types (PDF, Python, Markdown)
                {
                    Log.info("Split Level 3: Document Specific Splitting");

                    break;
                }
                case 4:
//                    Semantic Splitting - Embedding walk based chunking
                {
                    Log.info("Split Level 4: Semantic Splitting");
                    break;
                }
                case 5:

//                     Agentic Splitting - Experimental method of splitting
//                     text with an agent-like system. Good for if you
//                     believe that token cost will trend to $0.00
                {
                    Log.info("Split Level 5: Agentic Splitting ");
                    break;
                }
                case 6:
//                     Alternative Representation Chunking + Indexing -
//                     Derivative representations of your raw text that will
//                     aid in retrieval and indexing
                {
                    Log.info("Split Level 6 Bonus Level:" +
                            " Alternative Representation Chunking + Indexing");
                    break;
                }
                default: {
                    Log.info("Split Level 3");
                    segments = splitLevel2(documentImportRequestDTO);
                }
            }
            return segments;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error importing document: " + e.getMessage());
            return null;
        }
    }

    /***
     * Recursive Character Text Splitting -
     * Recursive chunking based on a list of separators
     */

    private static List<TextSegment> splitLevel2(DocumentImportRequestDTO documentImportRequestDTO) {

        List<TextSegment> segments = null;

        // Define the sub-splitter for splitting segments larger than max tokens

        // Create a recursive splitter with a sub-splitting mechanism
        DocumentSplitter splitter = DocumentSplitters.recursive(
                documentImportRequestDTO.getMaxSegmentSizeInTokens(), // Max segment size
                documentImportRequestDTO.getMaxOverlapSizeInTokens(), // Max overlap size
                new OpenAiTokenizer()
        );

        // Perform the split
        segments = splitter.split(documentImportRequestDTO.getDocument());
        Log.info("Executed splitLevel2: Total segments generated: " + segments.size());
        return segments;

    }

    private String parseExcelToText(Path filePath) throws Exception {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(new File(filePath.toString()));
             Workbook workbook = WorkbookFactory.create(fis)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = cell.toString();
                        content.append(cellValue).append("\t"); // Tab to separate cells (optional)
                    }
                    content.append("\n"); // Newline for the row
                }
            }
        }
        return content.toString();
    }

    public void importExcelDocument(String fileName) {
        Path documentPath = toPath(fileName);

        try {
            String excelText = parseExcelToText(documentPath);
            Document document = new DefaultDocument(excelText);

            // Split document into segments 100 tokens each
            DocumentSplitter documentsplitter = DocumentSplitters.recursive(
                    maxChunkSize,
                    maxOverlap,
                    new OpenAiTokenizer()
            );

            List<TextSegment> segments = documentsplitter.split(document);
            // Embed segments (convert them into vectors that represent the meaning) using embedding model
            EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

            // Store embeddings into embedding store for further search / retrieval
            EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                    .host(CLOUD_REDIS_HOST)
                    .port(CLOUD_REDIS_PORT)
                    .dimension(384)
                    .user("default")
                    .password("npGGTMCQjvxUizqBp4V3u6uFmdzPTogU")
                    .indexName("SIZWE2025")
                    .build();
            embeddingStore.addAll(embeddings, segments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings({})
    public void importDocuments() {
        Path documentPath = toPath("data/Sizwe-Hosmed-Full-Member-Guide-Nov-2023.pdf");
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());

        try {
            List<String> stringSegments = splitDocument(documentPath.toString(),300, 20);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Split document into segments 100 tokens each
        DocumentSplitter documentsplitter = DocumentSplitters.recursive(
                maxChunkSize,
                maxOverlap,
                new OpenAiTokenizer()
        );

        List<TextSegment> segments = documentsplitter.split(document);
        // Embed segments (convert them into vectors that represent the meaning) using embedding model
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store embeddings into embedding store for further search / retrieval
        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host(CLOUD_REDIS_HOST)
                .port(CLOUD_REDIS_PORT)
                .dimension(384)
                .user("default")
                .password("npGGTMCQjvxUizqBp4V3u6uFmdzPTogU")
                .indexName("SIZWE2025")
                .build();
        embeddingStore.addAll(embeddings, segments);

        // Embed the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();








//        // Find relevant embeddings in embedding store by semantic similarity
//        // You can play with parameters below to find a sweet spot for your specific use case
//        int maxResults = 3;
//        double minScore = 0.7;
//        List<EmbeddingMatch<TextSegment>> relevantEmbeddings
//                = embeddingStore.findRelevant(questionEmbedding, maxResults, minScore);

//        // Create a prompt for the model that includes question and relevant embeddings
//        PromptTemplate promptTemplate = PromptTemplate.from(
//                "Answer the following question to the best of your ability:\n"
//                        + "\n"
//                        + "Question:\n"
//                        + "{{question}}\n"
//                        + "\n"
//                        + "Base your answer on the following information:\n"
//                        + "{{information}}");
//
//        String information = relevantEmbeddings.stream()
//                .map(match -> match.embedded().text())
//                .collect(joining("\n\n"));
//
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("question", question);
//        variables.put("information", information);
//
//        Prompt prompt = promptTemplate.apply(variables);
//
//        // Send the prompt to the OpenAI chat model
//        ChatLanguageModel chatModel = OpenAiChatModel.builder()
//                .apiKey(OPENAI_API_KEY)
//                .modelName(GPT_4)
//                .timeout(Duration.ofSeconds(60))
//                .build();
//        AiMessage aiMessage = chatModel.generate(prompt.toUserMessage()).content();
//
//        // See an answer from the model
//        String answer = aiMessage.text();
//        System.out.println();
//        System.out.println("----------------------------------------------------------------------------------------");
//        System.out.println(question);
//
//        System.out.println("----------------------------------------------------------------------------------------");
//        System.out.println(answer);
//        System.out.println("-----------------------------------------------------------------------------------------");
//        System.out.println();
//        AnswerDTO answerDTO = new AnswerDTO();
//        answerDTO.setQuestion(question);
//        answerDTO.setAnswer(answer);
//        return answerDTO;






    }

    public String updateRagFromUserSuggestion(String userSuggestion) {

        Document document = new DefaultDocument(userSuggestion);


        // Split document into segments 100 tokens each
        DocumentSplitter documentsplitter = DocumentSplitters.recursive(
                300,
                0,
                new OpenAiTokenizer()
        );

        List<TextSegment> segments = documentsplitter.split(document);
        // Embed segments (convert them into vectors that represent the meaning) using embedding model
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store embeddings into embedding store for further search / retrieval
        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host(CLOUD_REDIS_HOST)
                .port(CLOUD_REDIS_PORT)
                .dimension(384)
                .user("default")
                .password("npGGTMCQjvxUizqBp4V3u6uFmdzPTogU")
                .indexName("SIZWE2025")
                .build();

        embeddingStore.addAll(embeddings, segments);
        return "Updated..";
    }
}
