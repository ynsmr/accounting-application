package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ProductService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final InvoiceProductService invoiceProductService;
    private final UserService userService;
    private final MapperUtil mapperUtil;


    @Override
    public ProductDto findById(Long productId) {
        return convertToDto(findProductById(productId));
    }

    @Override
    public List<ProductDto> listAllProducts() {
        return listAllProductsByCompanyId(userService.getLoggedInUser().getCompany().getId()).stream()
                .filter(product -> product.getQuantityInStock() >= 1)
                .sorted(Comparator.comparing((ProductDto product) -> product.getCategory().getDescription()).thenComparing(ProductDto::getName))
                .collect(Collectors.toList());

    }

    @Override
    public void deleteProduct(Long productId) {
        softDeleteProduct(findProductById(productId));
    }

    @Override
    public void saveProduct(ProductDto productDto) {
        productRepository.save(convertToEntity(productDto));
    }

    @Override
    public void updateProduct(ProductDto productDto) {
        productRepository.save(convertToEntity(productDto));
    }

    @Override
    public boolean categoryHasProduct(Long categoryId) {
        return productRepository.existsByCategory_Id(categoryId);
    }

    @Override
    public List<ProductDto> listAllProductsByCompanyId(Long companyId) {
        return productRepository.findProductsByCategory_Company_Id(companyId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product){
        return mapperUtil.convert(product, new ProductDto());
    }
    
    private Product convertToEntity(ProductDto productDto){
        return mapperUtil.convert(productDto, new Product());
    }
    
    private Product findProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("No product found with id: " + productId));
    }
    
    private void softDeleteProduct(Product product){
        product.setIsDeleted(true);
        productRepository.save(product);
    }
}
