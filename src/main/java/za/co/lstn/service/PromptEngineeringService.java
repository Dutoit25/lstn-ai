package za.co.lstn.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import za.co.lstn.dto.ExtractEmailContentDTO;
import za.co.lstn.prompt.SimpelPromptTemplate;

@ApplicationScoped
public class PromptEngineeringService {

    @Inject
    SimpelPromptTemplate simpelPromptTemplate;

    public ExtractEmailContentDTO selectBestPrompt
            (ExtractEmailContentDTO extractEmailContentDTO) {
        ExtractEmailContentDTO emailReponse;
        switch (extractEmailContentDTO.getCategory()) {
            case PRODUCT_QUESTIONS:
                emailReponse = simpelPromptTemplate.
                        createPrompt(extractEmailContentDTO);
                break;
            case BILLING_PROBLEMS:
                emailReponse = simpelPromptTemplate.
                        createPrompt(extractEmailContentDTO);
                break;
            case TECHNICAL_SUPPORT:
                emailReponse = simpelPromptTemplate.
                        createPrompt(extractEmailContentDTO);
                break;
            case CLAIM_ISSUES:
                emailReponse = simpelPromptTemplate.
                        createPrompt(extractEmailContentDTO);
                break;
            default:
                emailReponse = simpelPromptTemplate.
                        createPrompt(extractEmailContentDTO);
                break;
        }

        return emailReponse;

    }


}
