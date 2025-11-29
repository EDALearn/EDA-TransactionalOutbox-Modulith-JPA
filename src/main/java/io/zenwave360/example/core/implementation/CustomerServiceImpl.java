package io.zenwave360.example.core.implementation;

import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.implementation.mappers.CustomerServiceMapper;
import io.zenwave360.example.core.implementation.mappers.EventsMapper;
import io.zenwave360.example.core.inbound.CustomerService;
import io.zenwave360.example.core.inbound.dtos.CustomerSearchCriteria;
import io.zenwave360.example.core.outbound.events.CustomerEventsProducer;
import io.zenwave360.example.core.outbound.jpa.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Service Implementation for managing [Customer]. */
@Service
@Transactional(readOnly = true)
@lombok.AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CustomerServiceMapper customerServiceMapper = CustomerServiceMapper.INSTANCE;

    private final CustomerRepository customerRepository;

    private final EventsMapper eventsMapper = EventsMapper.INSTANCE;

    private final CustomerEventsProducer eventsProducer;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Customer createCustomer(Customer input) {
        log.debug("[CRUD] Request to save Customer: {}", input);
        var customer = customerServiceMapper.update(new Customer(), input);
        customer = customerRepository.save(customer);
        // TODO: may need to reload the entity to fetch relationships 'mapped by id'
        // emit events
        var customerEvent = eventsMapper.asCustomerEvent(customer);
        eventsProducer.onCustomerEvent(customerEvent);
        return customer;
    }

    public Optional<Customer> getCustomer(Long id) {
        log.debug("[CRUD] Request to get Customer : {}", id);
        var customer = customerRepository.findById(id);
        return customer;
    }

    @Transactional
    public Optional<Customer> updateCustomer(Long id, Customer input) {
        log.debug("Request updateCustomer: {} {}", id, input);

        var customer = customerRepository.findById(id).map(existingCustomer -> {
            return customerServiceMapper.update(existingCustomer, input);
        }).map(customerRepository::save);
        if (customer.isPresent()) {
            // emit events
            var customerEvent = eventsMapper.asCustomerEvent(customer.get());
            eventsProducer.onCustomerEvent(customerEvent);
        }
        return customer;
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.debug("[CRUD] Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
        // emit events
        var customerEvent = eventsMapper.asCustomerEvent(id);
        eventsProducer.onCustomerDeletedEvent(customerEvent);
    }

    public Page<Customer> searchCustomers(CustomerSearchCriteria input, Pageable pageable) {
        log.debug("Request searchCustomers: {} {}", input, pageable);

        var customers = customerRepository.findAll(pageable);
        return customers;
    }

}
