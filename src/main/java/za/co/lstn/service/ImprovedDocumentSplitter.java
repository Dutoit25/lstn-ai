package za.co.lstn.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import edu.stanford.nlp.pipeline.*;
import java.util.Properties;

public class ImprovedDocumentSplitter {

    public static List<String> splitDocument(String filePath, int chunkSize, int chunkOverlap) throws Exception {
        PDDocument document = PDDocument.load(new java.io.File(filePath));
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Sentence Splitting
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument doc = new CoreDocument(text);
        pipeline.annotate(doc);

        List<String> sentences = new ArrayList<>();
        for (CoreSentence sentence : doc.sentences()) {
            sentences.add(sentence.text());
        }

        // Chunking with Overlap
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int currentChunkLength = 0;
        int sentenceIndex = 0;

        while (sentenceIndex < sentences.size()) {
            String sentence = sentences.get(sentenceIndex);
            int sentenceLength = sentence.length();

            if (currentChunkLength + sentenceLength <= chunkSize) {
                currentChunk.append(sentence).append(" ");
                currentChunkLength += sentenceLength + 1;
                sentenceIndex++;
            } else {
                chunks.add(currentChunk.toString().trim());

                // Overlap
                currentChunk = new StringBuilder();
                currentChunkLength = 0;
                sentenceIndex = Math.max(0, sentenceIndex - calculateOverlapSentences(sentences, sentenceIndex, chunkOverlap, chunkSize));

            }
        }
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private static int calculateOverlapSentences(List<String> sentences, int currentIndex, int overlap, int chunkSize){
        int overlapSentences = 0;
        int overlapLength = 0;
        while(overlapLength < overlap && currentIndex - overlapSentences > 0){
            overlapSentences++;
            overlapLength += sentences.get(currentIndex - overlapSentences).length();
        }
        return overlapSentences;
    }
}
