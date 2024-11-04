package io.zenwave360.example.adapters.web;

import io.zenwave360.example.adapters.web.model.CustomerSearchCriteriaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpMethod.*;

/**
* Integration tests for the {@link CustomerApi} REST controller.
*/
class CustomerApiIntegrationTest extends BaseWebTestClientTest {



    /**
    * Test: createCustomer for OK.
    */
    @Test
    void testCreateCustomer_201() {
        var requestBody = """
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

        webTestClient.method(POST).uri("/api/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isEqualTo(201)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.version").isNotEmpty()
            .jsonPath("$.name").isNotEmpty()
            .jsonPath("$.email").isNotEmpty()
            .jsonPath("$.addresses").isNotEmpty()
            .jsonPath("$.addresses").isArray()
            .jsonPath("$.addresses[0].street").isNotEmpty()
            .jsonPath("$.addresses[0].city").isNotEmpty()
            .jsonPath("$.paymentMethods").isNotEmpty()
            .jsonPath("$.paymentMethods").isArray()
            .jsonPath("$.paymentMethods[0].id").isNotEmpty()
            .jsonPath("$.paymentMethods[0].version").isNotEmpty()
            .jsonPath("$.paymentMethods[0].type").isNotEmpty()
            .jsonPath("$.paymentMethods[0].cardNumber").isNotEmpty();
    }

    /**
    * Test: getCustomer for OK.
    */
    @Test
    void testGetCustomer_200() {
        var id = "1";

        webTestClient.method(GET).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.version").isNotEmpty()
            .jsonPath("$.name").isNotEmpty()
            .jsonPath("$.email").isNotEmpty()
            .jsonPath("$.addresses").isNotEmpty()
            .jsonPath("$.addresses").isArray()
            .jsonPath("$.addresses[0].street").isNotEmpty()
            .jsonPath("$.addresses[0].city").isNotEmpty()
            .jsonPath("$.paymentMethods").isNotEmpty()
            .jsonPath("$.paymentMethods").isArray()
            .jsonPath("$.paymentMethods[0].id").isNotEmpty()
            .jsonPath("$.paymentMethods[0].version").isNotEmpty()
            .jsonPath("$.paymentMethods[0].type").isNotEmpty()
            .jsonPath("$.paymentMethods[0].cardNumber").isNotEmpty();
    }

    /**
    * Test: updateCustomer for OK.
    */
    @Test
    void testUpdateCustomer_200() {
        var id = "1";
        var requestBody = """
                {
                  "id": 1,
                  "version": 1,
                  "email": "john.doe@example.com",
                  "name": "John Doe",
                  "addresses": [
                    {
                      "city": "Anytown",
                      "street": "123 Main St"
                    }
                  ],
                  "paymentMethods": [
                    {
                      "type": "VISA",
                      "customerId": 1,
                      "cardNumber": "1234567890123456"
                    }
                  ]
                }
                """;

        webTestClient.method(PUT).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.version").isNotEmpty()
            .jsonPath("$.name").isNotEmpty()
            .jsonPath("$.email").isNotEmpty()
            .jsonPath("$.addresses").isNotEmpty()
            .jsonPath("$.addresses").isArray()
            .jsonPath("$.addresses[0].street").isNotEmpty()
            .jsonPath("$.addresses[0].city").isNotEmpty()
            .jsonPath("$.paymentMethods").isNotEmpty()
            .jsonPath("$.paymentMethods").isArray()
            .jsonPath("$.paymentMethods[0].id").isNotEmpty()
            .jsonPath("$.paymentMethods[0].version").isNotEmpty()
            .jsonPath("$.paymentMethods[0].type").isNotEmpty()
            .jsonPath("$.paymentMethods[0].cardNumber").isNotEmpty();
    }

    /**
    * Test: deleteCustomer for OK.
    */
    @Test
    void testDeleteCustomer_204() {
        var id = "1";

        webTestClient.method(DELETE).uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(204);
    }

    /**
    * Test: searchCustomers for OK.
    */
    @Test
    void testSearchCustomers_201() {
        CustomerSearchCriteriaDTO requestBody = new CustomerSearchCriteriaDTO();
        requestBody.setName("Jane Doe");
        requestBody.setEmail("jane.doe@example.com");
        requestBody.setCity("Othertown");
        requestBody.setState("NY");
        var page = "0";
        var limit = "10";
        var sort = "name:asc";

        webTestClient.method(POST).uri(uriBuilder -> uriBuilder.path("/api/customers/search").queryParam("page", page).queryParam("limit", limit).queryParam("sort", sort).build(page, limit, sort))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isEqualTo(201)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.totalPages").isNotEmpty()
            .jsonPath("$.number").isNotEmpty()
            .jsonPath("$.numberOfElements").isNotEmpty()
            .jsonPath("$.size").isNotEmpty()
            .jsonPath("$.content").isNotEmpty()
            .jsonPath("$.content").isArray()
            .jsonPath("$.content[0].id").isNotEmpty()
            .jsonPath("$.content[0].version").isNotEmpty()
            .jsonPath("$.content[0].name").isNotEmpty()
            .jsonPath("$.content[0].email").isNotEmpty()
            .jsonPath("$.content[0].addresses").isNotEmpty()
            .jsonPath("$.content[0].paymentMethods").isNotEmpty()
            .jsonPath("$.totalElements").isNotEmpty();
    }

}
