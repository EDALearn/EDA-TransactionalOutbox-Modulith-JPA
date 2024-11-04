package io.zenwave360.example._apimock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.zenwave360.example.adapters.web.model.CustomerDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class WireMockTest {

    static Map<String, Object> loadYaml(String classpathUrl) {
        var yaml = new Yaml();
        try (InputStream in = new ClassPathResource(classpathUrl).getInputStream()) {
            return yaml.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static OpenApiWireMockServer openAPIWireMockServer = new OpenApiWireMockServer(
            WireMockConfiguration.wireMockConfig().dynamicPort(),
            new CustomersApiMock(),
            loadYaml("public/apis/openapi.yml"));

    @BeforeEach
    void setup() throws IOException {
        openAPIWireMockServer.start();
    }

    @AfterEach
    void teardown() {
        openAPIWireMockServer.stop();
    }

    @Test
    void testCRUD() throws JsonProcessingException {
        var baseUrl = getBaseUrl();
        ObjectMapper objectMapper = new ObjectMapper();
        //ignore nulls
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        RestTemplate restTemplate = new RestTemplate();
        var request = """
                {
                    "name": "John Doe",
                    "email": "johndoe@email.com"
                }
                """;
        ResponseEntity<CustomerDTO> response = null;

        System.out.println("Creating customer");
        response = restTemplate.postForEntity(baseUrl + "/customers", new HttpEntity<>(request, getHeaders()), CustomerDTO.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Long id = response.getBody().getId();

        System.out.println("Fetching customer");
        response = restTemplate.getForEntity(baseUrl + "/customers/{id}", CustomerDTO.class, response.getBody().getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response.getBody());

        System.out.println("Updating customer");
        response.getBody().setName(RandomStringUtils.randomAlphabetic(10));
        request = objectMapper.writeValueAsString(response.getBody());
        response = restTemplate.exchange(baseUrl + "/customers/{id}", HttpMethod.PUT, new HttpEntity<>(request, getHeaders()), CustomerDTO.class, response.getBody().getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response.getBody());

        System.out.println("Deleting customer");
        response = restTemplate.exchange(baseUrl + "/customers/{id}", HttpMethod.DELETE, new HttpEntity<>(null, getHeaders()), CustomerDTO.class, response.getBody().getId());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        System.out.println("Fetching customer (Not Found)");
        try {
            response = restTemplate.getForEntity(baseUrl + "/customers/{id}", CustomerDTO.class, id);
            Assertions.fail("Customer Found");
        } catch (HttpClientErrorException.NotFound e) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }

    }


    String getBaseUrl() {
        return openAPIWireMockServer.baseUrl();
    }

    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
