package io.zenwave360.example.adapters.web.mappers;

import io.zenwave360.example.adapters.web.model.CustomerDTO;
import io.zenwave360.example.adapters.web.model.CustomerPaginatedDTO;
import io.zenwave360.example.adapters.web.model.CustomerSearchCriteriaDTO;
import io.zenwave360.example.core.domain.Customer;
import io.zenwave360.example.core.inbound.dtos.CustomerSearchCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(uses = BaseMapper.class)
public interface CustomerDTOsMapper {

    CustomerDTOsMapper INSTANCE = Mappers.getMapper(CustomerDTOsMapper.class);

    // request mappings
    CustomerSearchCriteria asCustomerSearchCriteria(CustomerSearchCriteriaDTO dto);

    Customer asCustomer(CustomerDTO dto);

    // response mappings

    List<CustomerDTO> asCustomerDTOList(List<Customer> entityList);

    CustomerPaginatedDTO asCustomerPaginatedDTO(Page<Customer> page);

    default Page<CustomerDTO> asCustomerDTOPage(Page<Customer> page) {
        return page.map(this::asCustomerDTO);
    }

    CustomerDTO asCustomerDTO(Customer entity);

}
