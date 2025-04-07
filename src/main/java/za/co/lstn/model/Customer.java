package za.co.lstn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Customer{
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String customerId;

}
