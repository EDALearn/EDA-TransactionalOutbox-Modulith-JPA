package io.zenwave360.example.core.implementation;

import io.zenwave360.example.config.ServicesInMemoryConfig;
import io.zenwave360.example.core.domain.Address;
import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.inbound.dtos.CustomerSearchCriteria;
import io.zenwave360.example.infrastructure.jpa.inmemory.CustomerRepositoryInMemory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Acceptance Test for CustomerService. */
class CustomerServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    ServicesInMemoryConfig context = new ServicesInMemoryConfig();

    CustomerServiceImpl customerService = context.customerService();

    CustomerRepositoryInMemory customerRepository = context.customerRepository();

    @BeforeEach
    void setUp() {
        context.reloadTestData();
    }

    @Test
    void createCustomerTest() {
        var input = new Customer();
         input.setName("name");
         input.setEmail("me@email.com");
         input.setAddresses(List.of(new Address() //
                 .setStreet("street")
                 .setCity("city")
         ));
        var customer = customerService.createCustomer(input);
        assertNotNull(customer.getId());
        assertTrue(customerRepository.containsEntity(customer));
    }

    @Test
    void getCustomerTest() {
        var id = 1L; // TODO fill id
        var customer = customerService.getCustomer(id);
        assertTrue(customer.isPresent());
    }

    @Test
    void updateCustomerTest() {
        var id = 1L; // TODO fill id
        var input = new Customer();
        input.setName("name");
        input.setEmail("me@email.com");
        input.setAddresses(List.of(new Address() //
                .setStreet("street")
                .setCity("city")
        ));
        assertTrue(customerRepository.containsKey(id));
        var customer = customerService.updateCustomer(id, input);
        assertTrue(customer.isPresent());
        assertTrue(customerRepository.containsEntity(customer.get()));
    }

    @Test
    void deleteCustomerTest() {
        var id = 1L; // TODO fill id
        assertTrue(customerRepository.containsKey(id));
        customerService.deleteCustomer(id);
        assertFalse(customerRepository.containsKey(id));
    }

    @Test
    void searchCustomersTest() {
        var searchCriteria = new CustomerSearchCriteria();
        var results = customerService.searchCustomers(searchCriteria, PageRequest.of(0, 10));
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
    }

}
