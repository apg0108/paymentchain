package com.businessdomain.customer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.businessdomain.customer.entities.CustomerProduct;
import com.businessdomain.customer.models.CustomerProductDto;

@Mapper
public interface CustomerProductMapper {
     CustomerProductMapper INSTANCE = Mappers.getMapper(CustomerProductMapper.class);

     CustomerProductDto customerProductToCustomerProductDto(CustomerProduct customerProduct);
     CustomerProduct customerProductDtoToCustomerProduct(CustomerProductDto customerProductDto);
}
