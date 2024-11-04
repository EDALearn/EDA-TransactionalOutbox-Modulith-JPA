package io.zenwave360.example.infrastructure.jpa.inmemory;

import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.outbound.jpa.CustomerRepository;

public class CustomerRepositoryInMemory extends InMemoryJpaRepository<Customer> implements CustomerRepository {

}
