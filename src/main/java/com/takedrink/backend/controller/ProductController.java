
package com.takedrink.backend.controller;

import com.takedrink.backend.dto.product.ProductRequest;
import com.takedrink.backend.dto.product.ProductResponse;
import com.takedrink.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // GET: Lấy danh sách sản phẩm
  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  // GET: Lấy chi tiết sản phẩm
  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(
          @PathVariable Long id
  ) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  // GET: Tìm kiếm sản phẩm theo tên
  @GetMapping("/search")
  public ResponseEntity<List<ProductResponse>> searchProductsByName(
          @RequestParam String keyword
  ) {
    return ResponseEntity.ok(
            productService.searchProductsByName(keyword)
    );
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
          @Valid @RequestBody ProductRequest request
  ) {
    ProductResponse createdProduct =
            productService.createProduct(request);

    URI location = URI.create(
            "/api/products/" + createdProduct.id()
    );

    return ResponseEntity
            .created(location)
            .body(createdProduct);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
          @PathVariable Long id,
          @Valid @RequestBody ProductRequest request
  ) {
    return ResponseEntity.ok(
            productService.updateProduct(id, request)
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(
          @PathVariable Long id
  ) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
