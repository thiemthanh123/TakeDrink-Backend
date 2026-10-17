package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.category.CategoryRequest;
import com.takedrink.backend.dto.category.CategoryResponse;
import com.takedrink.backend.entity.Category;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.exception.CategoryNotFoundException;
import com.takedrink.backend.repository.CategoryRepository;
import com.takedrink.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  public CategoryResponse getCategoryById(Long id) {
    return toResponse(findCategoryById(id));
  }

  @Override
  @Transactional
  public CategoryResponse createCategory(CategoryRequest request) {
    validateRequest(request);

    String name = request.name().trim();

    if (categoryRepository.existsByNameIgnoreCase(name)) {
      throw new BadRequestException(
              "Category name already exists: " + name
      );
    }

    Category category = new Category();
    category.setName(name);
    category.setDescription(request.description());
    category.setActive(true);

    Category savedCategory = categoryRepository.save(category);

    return toResponse(savedCategory);
  }

  @Override
  @Transactional
  public CategoryResponse updateCategory(
          Long id,
          CategoryRequest request
  ) {
    validateRequest(request);

    Category category = findCategoryById(id);
    String name = request.name().trim();

    if (categoryRepository.existsByNameIgnoreCaseAndIdNot(
            name, id
    )) {
      throw new BadRequestException(
              "Category name already exists: " + name
      );
    }

    category.setName(name);
    category.setDescription(request.description());

    Category updatedCategory = categoryRepository.save(category);

    return toResponse(updatedCategory);
  }

  @Override
  @Transactional
  public CategoryResponse updateCategoryStatus(
          Long id,
          Boolean active
  ) {
    if (active == null) {
      throw new BadRequestException(
              "Category active status must not be null."
      );
    }

    Category category = findCategoryById(id);
    category.setActive(active);

    Category updatedCategory = categoryRepository.save(category);

    return toResponse(updatedCategory);
  }

  private Category findCategoryById(Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException(
              "Category ID must be a positive number."
      );
    }

    return categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
  }

  private void validateRequest(CategoryRequest request) {
    if (request == null) {
      throw new BadRequestException(
              "Category request must not be null."
      );
    }
  }

  private CategoryResponse toResponse(Category category) {
    return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
    );
  }
}
