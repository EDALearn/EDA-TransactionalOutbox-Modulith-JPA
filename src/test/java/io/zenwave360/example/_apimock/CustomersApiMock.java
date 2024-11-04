package io.zenwave360.example._apimock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

import java.util.HashMap;
import java.util.Map;

class CustomersApiMock {

    int id = 0;
    ObjectMapper objectMapper = new ObjectMapper();
    Map<Integer, Object> customers = new HashMap<>();

    public Response createCustomer(Request request, int statusCode, String contentType) throws JsonProcessingException {
        var customer = objectMapper.readValue(request.getBodyAsString(), Map.class);
        customer.put("id", ++id);
        customers.put(id, customer);
        return response(statusCode, contentType, customer);
    }

    public Response getCustomer(Request request, int statusCode, String contentType) {
        var id = Integer.parseInt(request.getPathParameters().get("id"));
        var customer = customers.get(id);
        return customer != null ? response(statusCode, contentType, customer) : response(404, contentType, null);
    }

    public Response updateCustomer(Request request, int statusCode, String contentType) throws JsonProcessingException {
        var id = Integer.parseInt(request.getPathParameters().get("id"));
        var customer = objectMapper.readValue(request.getBodyAsString(), Map.class); // TODO: merge instead of replace
        customers.put(id, customer);
        return response(statusCode, contentType, customer);
    }

    public Response deleteCustomer(Request request, int statusCode, String contentType) throws JsonProcessingException {
        var id = Integer.parseInt(request.getPathParameters().get("id"));
        customers.remove(id);
        return response(statusCode, "plain/text", "");
    }

    public Response searchCustomers(Request request, int statusCode, String contentType) throws JsonProcessingException {
        // TODO: filter and paginate
        return response(statusCode, contentType, customers.values());
    }


    private Response response(int statusCode, String contentType, Object body) {
        try {
            return response(statusCode, contentType, objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            return response(500, contentType, e.getMessage());
        }
    }

    private Response response(int statusCode, String contentType, String body) {
        return Response.response()
                .status(statusCode)
                .headers(new com.github.tomakehurst.wiremock.http.HttpHeaders(new HttpHeader("Content-Type", contentType)))
                .body(body)
                .build();
    }
}
