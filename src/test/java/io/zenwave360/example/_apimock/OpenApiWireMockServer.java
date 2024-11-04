package io.zenwave360.example._apimock;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.wiremock.WireMockRequest;
import com.atlassian.oai.validator.wiremock.WireMockResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class OpenApiWireMockServer extends WireMockServer {

    private static final Logger log = LoggerFactory.getLogger(OpenApiWireMockServer.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenApiInteractionValidator validator;
    private final Object mockService;

    public OpenApiWireMockServer(WireMockConfiguration options, Object mockService, Map<String, Object> openapi) {
        this(options, mockService, OpenApiInteractionValidator.createForInlineApiSpecification(asJson(openapi)).build(), openapi);
    }

    private OpenApiWireMockServer(WireMockConfiguration options, Object mockService, OpenApiInteractionValidator validator, Map<String, Object> openapi) {
        super(options.extensions(new DynamicResponseTransformer(mockService), new RequestValidator(validator), new ResponseValidator(validator)));
        this.validator = validator;
        this.mockService = mockService;
        configureStubsFromOpenApi(openapi);
    }

    private void configureStubsFromOpenApi(Map<String, Object> openapi) {
        Map<String, Map<String, Object>> paths = (Map) openapi.get("paths");

        for (var pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            Map<String, Object> methods = pathEntry.getValue();
            for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                var method = methodEntry.getKey();
                var operation = (Map<String, Object>) methodEntry.getValue();
                var operationId = (String) operation.get("operationId");
                var responseEntry = jsonPathFirst(operation, "$.responses");
                var statusCode = responseEntry.getKey();
                var contentEntry = jsonPathFirst(operation, "$.responses." + responseEntry.getKey() + ".content");
                var contentType = contentEntry != null ? contentEntry.getKey() : null;

                var requestHandlerMethod = getRequestHandlerMethod(mockService, operationId);
                if (requestHandlerMethod != null) {
                    log.info("Configuring Dynamic Request Handler Stub for %s [%s %s %s]".formatted(operationId, method.toUpperCase(), path, statusCode));
                    stubFor(WireMock.request(method.toUpperCase(), WireMock.urlPathTemplate(path))
                            .willReturn(WireMock.aResponse()
                                    .withTransformers(DynamicResponseTransformer.class.getName(), ResponseValidator.class.getName())
                                    .withTransformerParameter("operationId", operationId)
                                    .withTransformerParameter("statusCode", responseEntry.getKey())
                                    .withTransformerParameter("contentType", contentType)
                            )
                    );
                    continue;
                }

                if (contentEntry == null) {
                    log.info("Configuring Static Stub (no content) for %s [%s %s %s]".formatted(operationId, method.toUpperCase(), path, statusCode));
                    stubFor(WireMock.request(method.toUpperCase(), WireMock.urlPathTemplate(path))
                            .willReturn(WireMock.aResponse()
                                    .withStatus(Integer.parseInt(statusCode))));
                    continue;
                }

                var example = ((Map) contentEntry.getValue()).get("example");
                var examplesFirstKey = jsonPathFirstKey((Map) contentEntry.getValue(), "$.examples");
                var examplesFirstValue = jsonPath((Map) contentEntry.getValue(), "$.examples." + examplesFirstKey + ".value");
                example = ObjectUtils.firstNonNull(example, examplesFirstValue);
                if (example != null) {
                    log.info("Configuring Static Stub for %s [%s %s %s]".formatted(operationId, method.toUpperCase(), path, statusCode));
                    stubFor(WireMock.request(method.toUpperCase(), WireMock.urlPathTemplate(path))
                            .willReturn(WireMock.aResponse()
                                    .withStatus(Integer.parseInt(statusCode))
                                    .withHeader("Content-Type", contentType)
                                    .withBody(asJson(example))));
                } else {
                    log.warn("No example found for: %s %s %s".formatted(method.toUpperCase(), path, statusCode));
                }
            }
        }
    }

    private static class RequestValidator extends StubRequestFilter {
        OpenApiInteractionValidator validator;

        public RequestValidator(OpenApiInteractionValidator validator) {
            this.validator = validator;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        public RequestFilterAction filter(Request request) {
            log.debug("Validating request {} {}", request.getMethod(), request.getUrl());
            var report = validator.validateRequest(WireMockRequest.of(request));
            if (report.hasErrors()) {
                ResponseDefinition response = new ResponseDefinition(400, report.toString());
                return RequestFilterAction.stopWith(response);
            }

            return RequestFilterAction.continueWith(request);
        }
    }


    private record ResponseValidator(OpenApiInteractionValidator validator) implements ResponseTransformerV2 {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        public Response transform(Response response, ServeEvent serveEvent) {
            var request = serveEvent.getRequest();
            var method = com.atlassian.oai.validator.model.Request.Method.valueOf(request.getMethod().getName());
            var report = validator.validateResponse(request.getUrl(), method, WireMockResponse.of(response));
            var errors = report.getMessages().stream().filter(message -> !message.getKey().equals("validation.response.status.unknown")).toList();
            if (!errors.isEmpty()) {
                return Response.response()
                        .status(500)
                        .body(report.toString())
                        .build();
            }
            return response;
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }


    private record DynamicResponseTransformer(Object service) implements ResponseTransformerV2 {

        @Override
        public String getName() {
            return DynamicResponseTransformer.class.getName();
        }

        @Override
        public Response transform(Response response, ServeEvent serveEvent) {
            var operationId = (String) serveEvent.getTransformerParameters().get("operationId");
            var requestHandlerMethod = getRequestHandlerMethod(service, operationId);
            var statusCode = (String) serveEvent.getTransformerParameters().get("statusCode");
            var contentType = (String) serveEvent.getTransformerParameters().get("contentType");
            com.github.tomakehurst.wiremock.http.Request request = serveEvent.getRequest();

            try {
                log.info("Invoking request handler method {} for {} {}", requestHandlerMethod.getName(), request.getMethod(), request.getUrl());
                var result = (Response) requestHandlerMethod.invoke(service, request, Integer.parseInt(statusCode), contentType);
                log.info("Request handler method {} returned {} {}", requestHandlerMethod.getName(), result.getStatus(), result.getBodyAsString());
                return result;
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Error invoking request handler method {}", requestHandlerMethod, e.getCause());
                return Response.response()
                        .status(500)
                        .body(e.getMessage())
                        .build();
            }
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }

    private <T> T jsonPath(Map model, String path) {
        try {
            return JsonPath.read(model, path);
        } catch (Exception e) {
            return null;
        }
    }

    private Map.Entry<String, Object> jsonPathFirst(Map model, String path) {
        Map<String, Object> results = jsonPath(model, path);
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.entrySet().iterator().next();
    }

    private String jsonPathFirstKey(Map model, String path) {
        Map.Entry<String, Object> entry = jsonPathFirst(model, path);
        return entry != null ? entry.getKey() : null;
    }

    private static Method getRequestHandlerMethod(Object mockService, String methodName) {
        try {
            return mockService.getClass().getMethod(methodName, Request.class, int.class, String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String asJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON", e);
            throw new RuntimeException(e);
        }
    }
}
