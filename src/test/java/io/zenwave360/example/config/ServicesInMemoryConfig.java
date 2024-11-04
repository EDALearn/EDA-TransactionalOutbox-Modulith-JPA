package io.zenwave360.example.config;

import io.zenwave360.example.core.domain.Address;
import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.domain.PaymentMethod;
import io.zenwave360.example.core.implementation.CustomerServiceImpl;
import io.zenwave360.example.core.inbound.CustomerService;
import io.zenwave360.example.core.outbound.events.EventsProducerInMemoryContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/** Services InMemory Config. It can be used standalone or with @SpringBootTest. */
@Configuration
@Profile("in-memory")
public class ServicesInMemoryConfig extends RepositoriesInMemoryConfig {

    protected final EventsProducerInMemoryContext eventsProducerInMemoryContext = new EventsProducerInMemoryContext();

    private ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            publishedEvents.add(event);
        }
    };

    protected final CustomerServiceImpl customerService = new CustomerServiceImpl(customerRepository(),
            eventsProducerInMemoryContext.customerEventsProducer(), applicationEventPublisher);

    @Bean
    public <T extends CustomerService> T customerService() {
        return (T) customerService;
    }

    static List<Customer> _customers;

    public void reloadTestData() {
        var testDataLoader = new TestDataLoader(List.of(Customer.class, Address.class, PaymentMethod.class));
        var customers = _customers != null ? _customers
                : testDataLoader.loadCollectionTestDataAsObjects(Customer.class);
        customerRepository().deleteAll();
        customerRepository().saveAll(customers);
    }

    public EventsProducerInMemoryContext getEventsProducerInMemoryContext() {
        return eventsProducerInMemoryContext;
    }

    private List<Object> publishedEvents = new ArrayList<>();

    public List<Object> getPublishedEvents() {
        return publishedEvents;
    }

}
