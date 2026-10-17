
package com.takedrink.backend.service;

import com.takedrink.backend.dto.order.CreateOrderRequest;
import com.takedrink.backend.dto.order.OrderResponse;
import com.takedrink.backend.dto.order.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {

  OrderResponse createOrder(CreateOrderRequest request);

  OrderResponse getOrderById(Long id);

  List<OrderResponse> getAllOrders();

  List<OrderResponse> getMyOrders();

  OrderResponse updateOrderStatus(
          Long orderId,
          UpdateOrderStatusRequest request
  );

  OrderResponse cancelMyOrder(Long orderId);
}
