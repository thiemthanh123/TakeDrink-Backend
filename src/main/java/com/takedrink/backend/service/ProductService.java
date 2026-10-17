package com.takedrink.backend.service;

import com.takedrink.backend.dto.product.ProductRequest;
import com.takedrink.backend.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {

  List<ProductResponse> getAllProducts();

  ProductResponse getProductById(Long id);

  List<ProductResponse> searchProductsByName(String keyword);

  ProductResponse createProduct(ProductRequest request);

  ProductResponse updateProduct(Long id, ProductRequest request);

  void deleteProduct(Long id);
}