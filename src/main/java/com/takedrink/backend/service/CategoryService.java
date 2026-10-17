package com.takedrink.backend.service;

import com.takedrink.backend.dto.category.CategoryRequest;
import com.takedrink.backend.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

  List<CategoryResponse> getAllCategories();

  CategoryResponse getCategoryById(Long id);

  CategoryResponse createCategory(CategoryRequest request);

  CategoryResponse updateCategory(Long id, CategoryRequest request);

  CategoryResponse updateCategoryStatus(Long id, Boolean active);
}
