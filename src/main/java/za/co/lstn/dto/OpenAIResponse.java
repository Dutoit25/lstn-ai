package za.co.lstn.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OpenAIResponse {
    private String content;
    private String run_id;
    private String thread_id;
}
