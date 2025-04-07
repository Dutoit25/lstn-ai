package za.co.lstn.main;
//import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
//import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
//import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class Main {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private static final String OPENAI_API_KEY = "";  // Replace with your actual OpenAI API key


    /**
     * This example demonstrates how to use low-level LangChain4j APIs to implement RAG.
     * Check other packages to see examples of using high-level API (AI Services).
     */

    public static void main(String[] args) {

        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .timeout(Duration.ofMinutes(5))
                .build();

    }

}