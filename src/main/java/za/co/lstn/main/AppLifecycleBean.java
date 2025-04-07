package za.co.lstn.main;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import za.co.lstn.model.Customer;
import za.co.lstn.model.CustomerRepository;

@ApplicationScoped
public class AppLifecycleBean {

    @Inject
    CustomerRepository customerRepository;


    void onStart(@Observes StartupEvent ev) {
        Log.info("The application is starting...");
       // Load customer data
        Customer customer = Customer.builder()
                        .id(7605230015082L)
                        .customerId("7605230015082")
                                .firstName("Elizma")
                                .lastName("Matysik")
                                .email("elizma@nomail.com")
                                .build();
        customerRepository.save(customer);

        customer = Customer.builder()
                .id(6107205095083L)
                .customerId("6107205095083")
                .firstName("Du Toit")
                .lastName("Appelcryn")
                .email("dutoit@nomail.com")
                .build();
        customerRepository.save(customer);

        customer = Customer.builder()
                .id(6807204095083L)
                .customerId("6807204095083")
                .firstName("Kerda")
                .lastName("Marais")
                .email("Kerda@nomail.com")
                .build();
        customerRepository.save(customer);

    }

    void onStop(@Observes ShutdownEvent ev) {
        Log.info("The application is stopping...");
    }

}