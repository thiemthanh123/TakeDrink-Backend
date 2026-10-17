package com.takedrink.backend.controller;

import com.takedrink.backend.dto.category.CategoryRequest;
import com.takedrink.backend.dto.category.CategoryResponse;
import com.takedrink.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    return ResponseEntity.ok(
            categoryService.getAllCategories()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategoryById(
          @PathVariable Long id
  ) {
    return ResponseEntity.ok(
            categoryService.getCategoryById(id)
    );
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
          @Valid @RequestBody CategoryRequest request
  ) {
    CategoryResponse createdCategory =
            categoryService.createCategory(request);

    URI location = URI.create(
            "/api/categories/" + createdCategory.id()
    );

    return ResponseEntity.created(location)
            .body(createdCategory);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
          @PathVariable Long id,
          @Valid @RequestBody CategoryRequest request
  ) {
    return ResponseEntity.ok(
            categoryService.updateCategory(id, request)
    );
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CategoryResponse> updateCategoryStatus(
          @PathVariable Long id,
          @RequestParam Boolean active
  ) {
    return ResponseEntity.ok(
            categoryService.updateCategoryStatus(id, active)
    );
  }
}
