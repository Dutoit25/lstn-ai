package za.co.lstn.shared;

import io.quarkus.logging.Log;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Scanner;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class Utils {

    public static final String OPENAI_API_KEY = getOrDefault(System.getenv("OPENAI_API_KEY"), "");

    public static void startConversationWith(Assistant assistant) {

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                Log.info("==================================================");
                Log.info("User: ");
                String userQuery = scanner.nextLine();
                Log.info("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = assistant.answer(userQuery);
                Log.info("==================================================");
                Log.info("Assistant: " + agentAnswer);
            }
        }
    }

    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = Utils.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
