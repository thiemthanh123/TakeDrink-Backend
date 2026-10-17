package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.product.ProductRequest;
import com.takedrink.backend.dto.product.ProductResponse;
import com.takedrink.backend.entity.Category;
import com.takedrink.backend.entity.Product;
import com.takedrink.backend.exception.BadRequestException;

import com.takedrink.backend.exception.ProductNotFoundException;
import com.takedrink.backend.exception.CategoryNotFoundException;
import com.takedrink.backend.repository.CategoryRepository;
import com.takedrink.backend.repository.ProductRepository;
import com.takedrink.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  public ProductResponse getProductById(Long id) {
    Product product = findProductById(id);
    return toResponse(product);
  }

  @Override
  public List<ProductResponse> searchProductsByName(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      throw new BadRequestException(
              "Product search keyword must not be blank."
      );
    }

    return productRepository
            .findByNameContainingIgnoreCase(keyword.trim())
            .stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  @Transactional
  public ProductResponse createProduct(ProductRequest request) {
    validateRequest(request);

    Category category = findActiveCategory(request.categoryId());

    Product product = new Product();
    applyRequest(product, request, category);

    Product savedProduct = productRepository.save(product);

    return toResponse(savedProduct);
  }

  @Override
  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    validateId(id);
    validateRequest(request);

    Product product = findProductById(id);
    Category category = findActiveCategory(request.categoryId());

    applyRequest(product, request, category);

    Product updatedProduct = productRepository.save(product);

    return toResponse(updatedProduct);
  }

  @Override
  @Transactional
  public void deleteProduct(Long id) {
    Product product = findProductById(id);
    productRepository.delete(product);
  }

  private Product findProductById(Long id) {
    validateId(id);

    return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
  }

  private void validateId(Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException(
              "Product ID must be a positive number."
      );
    }
  }

  private void validateRequest(ProductRequest request) {
    if (request == null) {
      throw new BadRequestException(
              "Product request must not be null."
      );
    }
  }

  private Category findActiveCategory(Long categoryId) {
    if (categoryId == null || categoryId <= 0) {
      throw new BadRequestException(
              "Category ID must be a positive number."
      );
    }

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    if (!Boolean.TRUE.equals(category.getActive())) {
      throw new BadRequestException(
              "Cannot assign an inactive category to a product."
      );
    }

    return category;
  }

  private void applyRequest(
          Product product,
          ProductRequest request,
          Category category
  ) {
    product.setName(request.name().trim());
    product.setDescription(request.description());
    product.setPrice(request.price());
    product.setImageUrl(request.imageUrl());
    product.setCategory(category);
    product.setAvailable(request.available());
  }

  private ProductResponse toResponse(Product product) {
    Category category = product.getCategory();

    return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImageUrl(),
            category != null ? category.getId() : null,
            category != null ? category.getName() : null,
            product.getAvailable(),
            product.getCreatedAt(),
            product.getUpdatedAt()
    );
  }
}