package za.co.lstn.tools.customer;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import za.co.lstn.dto.CustomerDTO;
import za.co.lstn.dto.EmailMessageDTO;
import za.co.lstn.service.CustomerDataService;

@ApplicationScoped
public class CustomerTools {
    @Inject
    CustomerDataService customerDataExtractorService;

    @Tool
    public CustomerDTO extractCustomerData(EmailMessageDTO email) {
        return customerDataExtractorService.extractCustomerData(email);
    }

    @Tool
    public  String extractIdNumber(String text) {
        return customerDataExtractorService.extractIdNumber(text);
    }

    @Tool
    public  String getCustomerDetails(CustomerDTO customerDTO) {
        //client api call
        return " ";
    }

}
