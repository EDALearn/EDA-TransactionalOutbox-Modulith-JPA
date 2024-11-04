package io.zenwave360.example.core.implementation.mappers;

import io.zenwave360.example.core.domain.Address;
import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.domain.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { BaseMapper.class })
public interface EventsMapper {

    EventsMapper INSTANCE = Mappers.getMapper(EventsMapper.class);

    io.zenwave360.example.core.outbound.events.dtos.Address asAddress(Address address);

    io.zenwave360.example.core.outbound.events.dtos.PaymentMethod asPaymentMethod(
            PaymentMethod paymentMethod);

    io.zenwave360.example.core.outbound.events.dtos.CustomerEvent asCustomerEvent(
            Customer customer);

    io.zenwave360.example.core.outbound.events.dtos.CustomerEvent asCustomerEvent(Long id);

}
