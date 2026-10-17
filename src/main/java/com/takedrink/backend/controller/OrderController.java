package com.takedrink.backend.controller;

import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.dto.order.CreateOrderRequest;
import com.takedrink.backend.dto.order.OrderResponse;
import com.takedrink.backend.dto.order.UpdateOrderStatusRequest;
import com.takedrink.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // Tạo đơn hàng
  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
          @Valid @RequestBody CreateOrderRequest request
  ) {
    OrderResponse orderResponse = orderService.createOrder(request);

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                    "Order created successfully",
                    orderResponse
            ));
  }

  // Xem chi tiết đơn hàng
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
          @PathVariable Long id
  ) {
    OrderResponse orderResponse = orderService.getOrderById(id);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Order retrieved successfully",
                    orderResponse
            )
    );
  }

  // Xem tất cả đơn hàng
  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
    List<OrderResponse> orders = orderService.getAllOrders();

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Orders retrieved successfully",
                    orders
            )
    );
  }

  // Xem đơn hàng của khách hàng hiện tại
  @GetMapping("/my-orders")
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
    List<OrderResponse> orders = orderService.getMyOrders();

    return ResponseEntity.ok(
            ApiResponse.success(
                    "My orders retrieved successfully",
                    orders
            )
    );
  }

  // Khách hàng hủy đơn hàng của chính mình
  @PatchMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse<OrderResponse>> cancelMyOrder(
          @PathVariable Long id
  ) {
    OrderResponse orderResponse = orderService.cancelMyOrder(id);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Order cancelled successfully",
                    orderResponse
            )
    );
  }

  // Nhân viên/quản lý cập nhật trạng thái đơn hàng
  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
          @PathVariable Long id,
          @Valid @RequestBody UpdateOrderStatusRequest request
  ) {
    OrderResponse orderResponse =
            orderService.updateOrderStatus(id, request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Order status updated successfully",
                    orderResponse
            )
    );
  }
}