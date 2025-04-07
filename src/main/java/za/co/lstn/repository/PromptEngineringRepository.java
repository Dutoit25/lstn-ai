package za.co.lstn.repository;

import za.co.lstn.dto.UserPromptRequest;


public interface PromptEngineringRepository {
    String findMatchingPrompt(String userPrompt);
    void savePrompt(UserPromptRequest userPrompt, String response);
}
