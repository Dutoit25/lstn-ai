package za.co.lstn.dto;

import dev.langchain4j.data.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentImportRequestDTO {
    private String documentName;
    private int splitterLevel;
    private int maxSegmentSizeInTokens;
    private int maxOverlapSizeInTokens;
    private Document document;

}
