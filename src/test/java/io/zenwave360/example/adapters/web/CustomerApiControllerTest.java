package io.zenwave360.example.adapters.web;

import io.zenwave360.example.adapters.web.model.*;
import io.zenwave360.example.config.ServicesInMemoryConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/** Test controller for CustomerApiController. */
class CustomerApiControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    ServicesInMemoryConfig context = new ServicesInMemoryConfig();

    CustomerApiController controller = new CustomerApiController(context.customerService());

    @BeforeEach
    void setUp() {
        context.reloadTestData();
    }

    @Test
    void createCustomerTest() {
        CustomerDTO reqBody = new CustomerDTO();
        reqBody.setName("John Doe");
        reqBody.setEmail("john.doe@example.com");
        reqBody.setAddresses(List.of(new AddressDTO("Anytown", "123 Main St")));
        reqBody.setPaymentMethods(List.of(new PaymentMethodDTO(PaymentMethodTypeDTO.VISA, "1234567890123456")));
        var response = controller.createCustomer(reqBody);
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getCustomerTest() {
        Long id = 1L;
        var response = controller.getCustomer(id);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateCustomerTest() {
        Long id = 1L;
        CustomerDTO reqBody = new CustomerDTO();
        reqBody.setName("John Doe");
        reqBody.setEmail("john.doe@example.com");
        reqBody.setAddresses(List.of(new AddressDTO("Anytown", "123 Main St")));
        reqBody.setPaymentMethods(List.of(new PaymentMethodDTO(PaymentMethodTypeDTO.VISA, "1234567890123456")));
        var response = controller.updateCustomer(id, reqBody);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteCustomerTest() {
        Long id = 1L;
        var response = controller.deleteCustomer(id);
        Assertions.assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void searchCustomersTest() {
        Optional<Integer> page = Optional.of(0);
        Optional<Integer> limit = Optional.of(10);
        Optional<List<String>> sort = Optional.of(List.of("name"));
        CustomerSearchCriteriaDTO reqBody = new CustomerSearchCriteriaDTO();
        var response = controller.searchCustomers(reqBody, page, limit, sort);
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

}
