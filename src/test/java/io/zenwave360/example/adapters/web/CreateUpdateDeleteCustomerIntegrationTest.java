package io.zenwave360.example.adapters.web;

import io.zenwave360.example.adapters.web.model.CustomerDTO;
import io.zenwave360.example.adapters.web.model.PaymentMethodTypeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpMethod.*;

/**
* Business Flow Test for: createCustomer, getCustomer, updateCustomer, deleteCustomer.
*/
class CreateUpdateDeleteCustomerIntegrationTest extends BaseWebTestClientTest {

    /**
    * Business Flow Test for: createCustomer, getCustomer, updateCustomer, deleteCustomer.
    */
    @Test
    void testCreateUpdateDeleteCustomerIntegrationTest() {
        // createCustomer: createCustomer
        var customerRequestBody0 = """
                {
                  "email": "jane.doe@example.com",
                  "name": "Jane Doe",
                  "addresses": [
                    {
                      "city": "Othertown",
                      "street": "456 Elm St"
                    }
                  ],
                  "paymentMethods": [
                    {
                      "type": "VISA",
                      "cardNumber": "6543210987654321"
                    }
                  ]
                }
                """;

        var createCustomerResponse0 = webTestClient.method(POST).uri("/api/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customerRequestBody0)
            .exchange()
            .expectStatus().isEqualTo(201)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(CustomerDTO.class);

    
        // getCustomer: getCustomer
        var id = createCustomerResponse0.getResponseBody().blockFirst().getId();

        var getCustomerResponse1 = webTestClient.method(GET).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(CustomerDTO.class);

    
        // updateCustomer: updateCustomer
        CustomerDTO customerRequestBody2 = getCustomerResponse1.getResponseBody().blockFirst();
        customerRequestBody2.setName("updated");
        customerRequestBody2.setEmail("updated@email.com");
        customerRequestBody2.getPaymentMethods().get(0).setType(PaymentMethodTypeDTO.VISA);

        var updateCustomerResponse2 = webTestClient.method(PUT).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customerRequestBody2)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(CustomerDTO.class);

    
        // deleteCustomer: deleteCustomer

        webTestClient.method(DELETE).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(204);


        // getCustomer: getCustomer (not found)
        webTestClient.method(GET).uri("/api/customers/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404);
    }


}
