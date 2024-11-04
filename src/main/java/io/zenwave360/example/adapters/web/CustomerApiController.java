package io.zenwave360.example.adapters.web;

import io.zenwave360.example.adapters.web.mappers.CustomerDTOsMapper;
import io.zenwave360.example.adapters.web.model.CustomerDTO;
import io.zenwave360.example.adapters.web.model.CustomerPaginatedDTO;
import io.zenwave360.example.adapters.web.model.CustomerSearchCriteriaDTO;
import io.zenwave360.example.core.inbound.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

/** REST controller for CustomerApi. */
@RestController
@RequestMapping("/api")
public class CustomerApiController implements CustomerApi {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private NativeWebRequest request;

    private CustomerService customerService;

    @Autowired
    public CustomerApiController setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
        return this;
    }

    private CustomerDTOsMapper mapper = CustomerDTOsMapper.INSTANCE;

    public CustomerApiController(CustomerService customerService) {

        this.customerService = customerService;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<CustomerDTO> createCustomer(CustomerDTO reqBody) {
        log.debug("REST request to createCustomer: {}", reqBody);
        var input = mapper.asCustomer(reqBody);
        var customer = customerService.createCustomer(input);
        CustomerDTO responseDTO = mapper.asCustomerDTO(customer);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @Override
    public ResponseEntity<CustomerDTO> getCustomer(Long id) {
        log.debug("REST request to getCustomer: {}", id);
        var customer = customerService.getCustomer(id);
        if (customer.isPresent()) {
            CustomerDTO responseDTO = mapper.asCustomerDTO(customer.get());
            return ResponseEntity.status(200).body(responseDTO);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<CustomerDTO> updateCustomer(Long id, CustomerDTO reqBody) {
        log.debug("REST request to updateCustomer: {}, {}", id, reqBody);
        var input = mapper.asCustomer(reqBody);
        var customer = customerService.updateCustomer(id, input);
        if (customer.isPresent()) {
            CustomerDTO responseDTO = mapper.asCustomerDTO(customer.get());
            return ResponseEntity.status(200).body(responseDTO);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long id) {
        log.debug("REST request to deleteCustomer: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<CustomerPaginatedDTO> searchCustomers(
            CustomerSearchCriteriaDTO reqBody,
            Optional<Integer> page, Optional<Integer> limit, Optional<List<String>> sort) {
        log.debug("REST request to searchCustomers: {}, {}, {}, {}", page, limit, sort, reqBody);
        var input = mapper.asCustomerSearchCriteria(reqBody);
        var customerPage = customerService.searchCustomers(input, pageOf(page, limit, sort));
        var responseDTO = mapper.asCustomerPaginatedDTO(customerPage);
        return ResponseEntity.status(200).body(responseDTO);
    }

    protected Pageable pageOf(Optional<Integer> page, Optional<Integer> limit, Optional<List<String>> sort) {
        Sort sortOrder = sort.map(s -> Sort.by(s.stream().map(sortParam -> {
            String[] parts = sortParam.split(":");
            String property = parts[0];
            Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
            return new Sort.Order(direction, property);
        }).toList())).orElse(Sort.unsorted());
        return PageRequest.of(page.orElse(0), limit.orElse(10), sortOrder);
    }

}
