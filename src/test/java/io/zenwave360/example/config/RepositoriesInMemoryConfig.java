package io.zenwave360.example.config;

import io.zenwave360.example.core.outbound.jpa.CustomerRepository;
import io.zenwave360.example.infrastructure.jpa.inmemory.CustomerRepositoryInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

// @Configuration
public class RepositoriesInMemoryConfig {

    protected final CustomerRepository customerRepository = new CustomerRepositoryInMemory();

    @Bean
    @Primary
    public <T extends CustomerRepository> T customerRepository() {
        return (T) customerRepository;
    }

}
