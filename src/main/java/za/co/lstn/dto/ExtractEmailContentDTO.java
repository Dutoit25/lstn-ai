package za.co.lstn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.lstn.enums.EmailCategory;
import za.co.lstn.enums.Sentiment;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ExtractEmailContentDTO {
        private CustomerDTO customerDTO;
        private EmailMessageDTO emailMessageDTO;
        private Sentiment sentiment;
        private EmailCategory category;
        private String emailReponse;
        private AnswerDTO ragAnswerDTO;

}
