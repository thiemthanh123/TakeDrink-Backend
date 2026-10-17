package com.takedrink.backend.exception;

import com.takedrink.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log =
          LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 1. Lỗi nghiệp vụ: email trùng, dữ liệu không hợp lệ...
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(
          BadRequestException exception
  ) {
    ApiResponse<Void> response =
            ApiResponse.error(exception.getMessage());

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
  }

  // 2. Không tìm thấy sản phẩm
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleProductNotFound(
          ProductNotFoundException exception
  ) {
    ApiResponse<Void> response =
            ApiResponse.error(exception.getMessage());

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
  }

  // 3. Không tìm thấy danh mục
  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleCategoryNotFound(
          CategoryNotFoundException exception
  ) {
    ApiResponse<Void> response =
            ApiResponse.error(exception.getMessage());

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
  }

  // 4. Lỗi validation từ @Valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
          MethodArgumentNotValidException exception
  ) {
    Map<String, String> fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage() != null
                            ? error.getDefaultMessage()
                            : "Invalid value",
                    (first, second) -> first
            ));

    ApiResponse<Void> response = ApiResponse.validationError(
            "Request validation failed",
            fieldErrors
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
  }

  // 5. Sai kiểu dữ liệu của tham số URL
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
          MethodArgumentTypeMismatchException exception
  ) {
    String message = "Invalid value for parameter: "
            + exception.getName();

    if (exception.getRequiredType() == Long.class) {
      message += ". Expected a valid integer ID.";
    }

    ApiResponse<Void> response = ApiResponse.error(message);

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
  }

  // 6. Lỗi không dự kiến
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
          Exception exception
  ) {
    log.error("Unexpected server error", exception);

    ApiResponse<Void> response = ApiResponse.error(
            "An unexpected error occurred"
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
          UnauthorizedException exception
  ) {
    ApiResponse<Void> response =
            ApiResponse.error(exception.getMessage());

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
  }

}