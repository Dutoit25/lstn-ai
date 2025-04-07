package za.co.lstn.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailAnalysisDTO {
    private String subject;
    private String from;
    private String to;
    private String body;


    @Override
    public String toString() {
        return "EmailAnalysisResultService{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}