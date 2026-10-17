package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.order.CreateOrderRequest;
import com.takedrink.backend.dto.order.OrderItemRequest;
import com.takedrink.backend.dto.order.OrderItemResponse;
import com.takedrink.backend.dto.order.OrderResponse;
import com.takedrink.backend.dto.order.UpdateOrderStatusRequest;
import com.takedrink.backend.entity.Address;
import com.takedrink.backend.entity.DiningTable;
import com.takedrink.backend.entity.Order;
import com.takedrink.backend.entity.OrderItem;
import com.takedrink.backend.entity.Product;
import com.takedrink.backend.entity.User;
import com.takedrink.backend.enums.OrderStatus;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.exception.OrderNotFoundException;
import com.takedrink.backend.exception.ProductNotFoundException;
import com.takedrink.backend.exception.UnauthorizedException;
import com.takedrink.backend.repository.AddressRepository;
import com.takedrink.backend.repository.DiningTableRepository;
import com.takedrink.backend.repository.OrderItemRepository;
import com.takedrink.backend.repository.OrderRepository;
import com.takedrink.backend.repository.ProductRepository;
import com.takedrink.backend.repository.UserRepository;
import com.takedrink.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ProductRepository productRepository;
  private final DiningTableRepository diningTableRepository;
  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  // =========================================================
  // CREATE ORDER
  // =========================================================

  @Override
  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) {
    validateRequest(request);

    String orderType = request.orderType()
            .trim()
            .toUpperCase(Locale.ROOT);

    User currentUser = switch (orderType) {
      case "DINE_IN", "TAKEAWAY" -> getOptionalAuthenticatedUser();
      case "DELIVERY" -> getAuthenticatedUser();
      default -> throw new BadRequestException(
              "Unsupported order type: " + orderType
      );
    };

    Order order = new Order();
    order.setOrderCode(generateOrderCode());
    order.setUser(currentUser);
    order.setOrderType(orderType);
    order.setStatus(OrderStatus.PENDING);
    order.setTotalAmount(BigDecimal.ZERO);
    order.setNote(request.note());

    configureOrderDestination(
            order,
            request,
            orderType,
            currentUser
    );

    BigDecimal totalAmount = BigDecimal.ZERO;
    List<OrderItem> orderItems = new ArrayList<>();

    for (OrderItemRequest itemRequest : request.items()) {
      Product product = productRepository
              .findById(itemRequest.productId())
              .orElseThrow(() ->
                      new ProductNotFoundException(
                              itemRequest.productId()
                      )
              );

      if (!Boolean.TRUE.equals(product.getAvailable())) {
        throw new BadRequestException(
                "Product is not available: " + product.getName()
        );
      }

      BigDecimal unitPrice = product.getPrice();

      if (unitPrice == null
              || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
        throw new BadRequestException(
                "Product has an invalid price: " + product.getName()
        );
      }

      BigDecimal subtotal = unitPrice.multiply(
              BigDecimal.valueOf(itemRequest.quantity())
      );

      OrderItem orderItem = new OrderItem();
      orderItem.setProduct(product);
      orderItem.setProductName(product.getName());
      orderItem.setUnitPrice(unitPrice);
      orderItem.setQuantity(itemRequest.quantity());
      orderItem.setSubtotal(subtotal);
      orderItem.setNote(itemRequest.note());

      orderItems.add(orderItem);
      totalAmount = totalAmount.add(subtotal);
    }

    order.setTotalAmount(totalAmount);

    Order savedOrder = orderRepository.save(order);

    for (OrderItem item : orderItems) {
      item.setOrder(savedOrder);
    }

    orderItemRepository.saveAll(orderItems);

    return toOrderResponse(savedOrder, orderItems);
  }

  // =========================================================
  // GET ORDER BY ID
  // =========================================================

  @Override
  public OrderResponse getOrderById(Long id) {
    validateId(id);

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (!isAuthenticated(authentication)) {
      throw new UnauthorizedException(
              "Authentication is required to view this order"
      );
    }

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    if (!hasStaffOrManagerRole(authentication)) {
      User currentUser = getAuthenticatedUser();

      if (order.getUser() == null
              || !order.getUser().getId()
              .equals(currentUser.getId())) {
        throw new OrderNotFoundException(id);
      }
    }

    List<OrderItem> items =
            orderItemRepository.findByOrderId(order.getId());

    return toOrderResponse(order, items);
  }

  // =========================================================
  // GET ALL ORDERS
  // =========================================================

  @Override
  public List<OrderResponse> getAllOrders() {
    List<Order> orders =
            orderRepository.findAllByOrderByCreatedAtDesc();

    return orders.stream()
            .map(order -> {
              List<OrderItem> items =
                      orderItemRepository.findByOrderId(order.getId());

              return toOrderResponse(order, items);
            })
            .collect(Collectors.toList());
  }

  // =========================================================
  // GET MY ORDERS
  // =========================================================

  @Override
  public List<OrderResponse> getMyOrders() {
    User currentUser = getAuthenticatedUser();

    List<Order> orders =
            orderRepository.findByUser_IdOrderByCreatedAtDesc(
                    currentUser.getId()
            );

    return orders.stream()
            .map(order -> {
              List<OrderItem> items =
                      orderItemRepository.findByOrderId(order.getId());

              return toOrderResponse(order, items);
            })
            .collect(Collectors.toList());
  }

  // =========================================================
  // CANCEL MY ORDER
  // =========================================================

  @Override
  @Transactional
  public OrderResponse cancelMyOrder(Long id) {
    validateId(id);

    User currentUser = getAuthenticatedUser();

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getUser() == null
            || !order.getUser().getId()
            .equals(currentUser.getId())) {
      throw new OrderNotFoundException(id);
    }

    OrderStatus currentStatus = order.getStatus();

    if (currentStatus != OrderStatus.PENDING
            && currentStatus != OrderStatus.CONFIRMED) {
      throw new BadRequestException(
              "Only pending or confirmed orders can be cancelled"
      );
    }

    order.setStatus(OrderStatus.CANCELLED);

    Order savedOrder = orderRepository.save(order);

    List<OrderItem> items =
            orderItemRepository.findByOrderId(savedOrder.getId());

    return toOrderResponse(savedOrder, items);
  }

  // =========================================================
  // UPDATE ORDER STATUS
  // =========================================================

  @Override
  @Transactional
  public OrderResponse updateOrderStatus(
          Long id,
          UpdateOrderStatusRequest request
  ) {
    validateId(id);

    if (request == null || request.status() == null) {
      throw new BadRequestException("Order status is required");
    }

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    OrderStatus targetStatus = request.status();

    if (!isValidStatusTransition(order.getStatus(), targetStatus)) {
      throw new BadRequestException(
              "Cannot change order status from "
                      + order.getStatus()
                      + " to "
                      + targetStatus
      );
    }

    order.setStatus(targetStatus);

    Order savedOrder = orderRepository.save(order);

    List<OrderItem> items =
            orderItemRepository.findByOrderId(savedOrder.getId());

    return toOrderResponse(savedOrder, items);
  }

  // =========================================================
  // AUTHENTICATION HELPERS
  // =========================================================

  private User getAuthenticatedUser() {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (!isAuthenticated(authentication)) {
      throw new UnauthorizedException(
              "Authentication is required"
      );
    }

    String email = authentication.getName();

    return userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() -> new UnauthorizedException(
                    "Authenticated user was not found"
            ));
  }

  private User getOptionalAuthenticatedUser() {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (!isAuthenticated(authentication)) {
      return null;
    }

    String email = authentication.getName();

    return userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() -> new UnauthorizedException(
                    "Authenticated user was not found"
            ));
  }

  private boolean isAuthenticated(Authentication authentication) {
    return authentication != null
            && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())
            && authentication.getName() != null
            && !authentication.getName().isBlank();
  }

  private boolean hasStaffOrManagerRole(
          Authentication authentication
  ) {
    if (authentication == null) {
      return false;
    }

    return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority ->
                    authority.equals("ROLE_STAFF")
                            || authority.equals("ROLE_MANAGER")
            );
  }

  // =========================================================
  // VALIDATION
  // =========================================================

  private void validateId(Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException(
              "Order ID must be positive"
      );
    }
  }

  private void validateRequest(CreateOrderRequest request) {
    if (request == null) {
      throw new BadRequestException(
              "Order request must not be null"
      );
    }

    if (request.orderType() == null
            || request.orderType().isBlank()) {
      throw new BadRequestException("Order type is required");
    }

    String orderType = request.orderType()
            .trim()
            .toUpperCase(Locale.ROOT);

    if (!List.of("DINE_IN", "TAKEAWAY", "DELIVERY")
            .contains(orderType)) {
      throw new BadRequestException(
              "Unsupported order type: " + orderType
      );
    }

    if (request.items() == null || request.items().isEmpty()) {
      throw new BadRequestException(
              "Order must contain at least one item"
      );
    }

    for (OrderItemRequest item : request.items()) {
      if (item == null) {
        throw new BadRequestException(
                "Order item must not be null"
        );
      }

      if (item.productId() == null || item.productId() <= 0) {
        throw new BadRequestException(
                "Product ID must be positive"
        );
      }

      if (item.quantity() == null || item.quantity() <= 0) {
        throw new BadRequestException(
                "Product quantity must be positive"
        );
      }
    }
  }

  // =========================================================
  // ORDER DESTINATION
  // =========================================================

  private void configureOrderDestination(
          Order order,
          CreateOrderRequest request,
          String orderType,
          User currentUser
  ) {
    switch (orderType) {
      case "DINE_IN" -> configureDineIn(order, request);

      case "DELIVERY" -> configureDelivery(
              order,
              request,
              currentUser
      );

      case "TAKEAWAY" -> {
        // Không cần bàn hoặc địa chỉ giao hàng.
      }

      default -> throw new BadRequestException(
              "Unsupported order type: " + orderType
      );
    }
  }

  private void configureDineIn(
          Order order,
          CreateOrderRequest request
  ) {
    if (request.diningTableId() == null) {
      throw new BadRequestException(
              "Dining table ID is required for DINE_IN orders"
      );
    }

    DiningTable diningTable = diningTableRepository
            .findById(request.diningTableId())
            .orElseThrow(() -> new BadRequestException(
                    "Dining table not found with ID: "
                            + request.diningTableId()
            ));

    if (!Boolean.TRUE.equals(diningTable.getActive())) {
      throw new BadRequestException("Dining table is not active");
    }

    order.setDiningTable(diningTable);
  }

  private void configureDelivery(
          Order order,
          CreateOrderRequest request,
          User currentUser
  ) {
    if (currentUser == null) {
      throw new UnauthorizedException(
              "Login is required for delivery orders"
      );
    }

    if (request.addressId() != null) {
      Address address = addressRepository
              .findById(request.addressId())
              .orElseThrow(() -> new BadRequestException(
                      "Address not found with ID: "
                              + request.addressId()
              ));

      if (address.getUser() == null
              || !address.getUser().getId()
              .equals(currentUser.getId())) {
        throw new BadRequestException(
                "The selected address does not belong to you"
        );
      }

      order.setAddress(address);
      order.setRecipientName(address.getRecipientName());
      order.setRecipientPhone(address.getPhone());
      order.setDeliveryAddress(formatAddress(address));

      return;
    }

    if (request.recipientName() == null
            || request.recipientName().isBlank()) {
      throw new BadRequestException(
              "Recipient name is required for delivery"
      );
    }

    if (request.recipientPhone() == null
            || request.recipientPhone().isBlank()) {
      throw new BadRequestException(
              "Recipient phone is required for delivery"
      );
    }

    if (request.deliveryAddress() == null
            || request.deliveryAddress().isBlank()) {
      throw new BadRequestException(
              "Delivery address is required"
      );
    }

    order.setAddress(null);
    order.setRecipientName(request.recipientName().trim());
    order.setRecipientPhone(request.recipientPhone().trim());
    order.setDeliveryAddress(request.deliveryAddress().trim());
  }

  private String formatAddress(Address address) {
    return java.util.stream.Stream.of(
                    address.getAddressLine(),
                    address.getWard(),
                    address.getDistrict(),
                    address.getProvince()
            )
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.joining(", "));
  }

  // =========================================================
  // ORDER STATUS TRANSITIONS
  // =========================================================

  private boolean isValidStatusTransition(
          OrderStatus currentStatus,
          OrderStatus targetStatus
  ) {
    if (currentStatus == null || targetStatus == null) {
      return false;
    }

    return switch (currentStatus) {
      case PENDING ->
              targetStatus == OrderStatus.CONFIRMED
                      || targetStatus == OrderStatus.CANCELLED;

      case CONFIRMED ->
              targetStatus == OrderStatus.PREPARING
                      || targetStatus == OrderStatus.CANCELLED;

      case PREPARING ->
              targetStatus == OrderStatus.READY;

      case READY ->
              targetStatus == OrderStatus.COMPLETED;

      case COMPLETED, CANCELLED -> false;
    };
  }

  // =========================================================
  // MAPPING
  // =========================================================

  private OrderResponse toOrderResponse(
          Order order,
          List<OrderItem> items
  ) {
    List<OrderItemResponse> itemResponses = items.stream()
            .map(item -> new OrderItemResponse(
                    item.getId(),
                    item.getProduct() != null
                            ? item.getProduct().getId()
                            : null,
                    item.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getSubtotal(),
                    item.getNote()
            ))
            .collect(Collectors.toList());

    return new OrderResponse(
            order.getId(),
            order.getOrderCode(),
            order.getOrderType(),
            order.getStatus().name(),
            order.getTotalAmount(),
            order.getRecipientName(),
            order.getRecipientPhone(),
            order.getDeliveryAddress(),
            order.getNote(),
            order.getDiningTable() != null
                    ? order.getDiningTable().getId()
                    : null,
            order.getAddress() != null
                    ? order.getAddress().getId()
                    : null,
            order.getCreatedAt(),
            itemResponses
    );
  }

  // =========================================================
  // ORDER CODE
  // =========================================================

  private String generateOrderCode() {
    return "TD-"
            + UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 20)
            .toUpperCase(Locale.ROOT);
  }
}
