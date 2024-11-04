package io.zenwave360.example.infrastructure.jpa;

import io.zenwave360.example.core.domain.Address;
import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.domain.PaymentMethod;
import io.zenwave360.example.core.domain.PaymentMethodType;
import io.zenwave360.example.core.outbound.jpa.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class CustomerRepositoryIntegrationTest extends BaseRepositoryIntegrationTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

    private Long id = 1L;

    @Test
    void findAllTest() {
        var results = customerRepository.findAll();
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    void findByIdTest() {
        var customer = customerRepository.findById(id).orElseThrow();
        Assertions.assertNotNull(customer.getId());
        Assertions.assertNotNull(customer.getVersion());
        Assertions.assertNotNull(customer.getCreatedBy());
        Assertions.assertNotNull(customer.getCreatedDate());
    }

    @Test
    void saveTest() {
        Customer customer = new Customer();
        customer.setName("Jane Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setAddresses(List.of(
                        new Address()
                                .setStreet("456 Elm St")
                                .setCity("Othertown")
                )
        );

        // OneToMany paymentMethods owner: true
        var paymentMethods = new PaymentMethod();
        paymentMethods.setType(PaymentMethodType.VISA);
        paymentMethods.setCardNumber("6543210987654321");
        customer.addPaymentMethods(paymentMethods);

        // Persist aggregate root
        var created = customerRepository.save(customer);

        // reloading to get relationships persisted by id
        entityManager.flush();
        entityManager.refresh(created);
        Assertions.assertNotNull(created.getId());
        Assertions.assertNotNull(created.getVersion());
        Assertions.assertNotNull(created.getCreatedBy());
        Assertions.assertNotNull(created.getCreatedDate());

        Assertions.assertTrue(customer.getPaymentMethods().stream().allMatch(item -> item.getId() != null));
    }

    @Test
    void updateTest() {
        var customer = customerRepository.findById(id).orElseThrow();
        customer.setName("updated");
        customer.setEmail("updated@email.com");


        customer = customerRepository.save(customer);
        Assertions.assertEquals("updated", customer.getName());
        Assertions.assertEquals("updated@email.com", customer.getEmail());
    }

    @Test
    void deleteTest() {
        customerRepository.deleteById(id);
        var notFound = customerRepository.findById(id);
        Assertions.assertFalse(notFound.isPresent());
    }

}
