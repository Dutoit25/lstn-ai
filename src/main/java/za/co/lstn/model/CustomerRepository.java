package za.co.lstn.model;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@ApplicationScoped
public class CustomerRepository {
 private Map <String, Customer> hashmap = new HashMap<> ();

 public Customer save(Customer customer){
  hashmap.put(customer.getCustomerId(), customer);
  return customer;
 }

 public Customer findById(String id){
    return hashmap.get(id);
 }

}
