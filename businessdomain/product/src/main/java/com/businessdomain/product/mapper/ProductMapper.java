package com.businessdomain.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.businessdomain.product.entities.Product;
import com.businessdomain.product.model.ProductDto;

@Mapper
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto productToProductDto(Product product);
    List<ProductDto> listProductToProductDto(List<Product> listProduct);
}
