package za.co.lstn.prompt;

import za.co.lstn.dto.ExtractEmailContentDTO;

public interface MasterPromptTemplate {

    ExtractEmailContentDTO createPrompt(ExtractEmailContentDTO extractEmailContentDTO);

}