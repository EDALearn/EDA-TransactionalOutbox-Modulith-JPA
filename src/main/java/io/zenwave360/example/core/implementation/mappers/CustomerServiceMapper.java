package io.zenwave360.example.core.implementation.mappers;

import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.inbound.dtos.CustomerSearchCriteria;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { BaseMapper.class })
public interface CustomerServiceMapper {

    CustomerServiceMapper INSTANCE = Mappers.getMapper(CustomerServiceMapper.class);

    @Mapping(target = "id", ignore = true)
    Customer update(@MappingTarget Customer entity, Customer input);

    @Mapping(target = "id", ignore = true)
    Customer update(@MappingTarget Customer entity, CustomerSearchCriteria input);

    @AfterMapping
    default void manageRelationships(@MappingTarget Customer entity) {
        if (entity.getPaymentMethods() != null) {
            entity.getPaymentMethods().forEach(paymentMethods -> paymentMethods.setCustomer(entity));
        }
    }

}
