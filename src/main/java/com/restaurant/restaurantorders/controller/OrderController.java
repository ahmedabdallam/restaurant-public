package com.restaurant.restaurantorders.controller;

import com.restaurant.restaurantorders.dto.request.CreateOrderRequest;
import com.restaurant.restaurantorders.dto.request.UpdateOrderStatusRequest;
import com.restaurant.restaurantorders.dto.response.ApiResponse;
import com.restaurant.restaurantorders.dto.response.OrderResponse;
import com.restaurant.restaurantorders.entity.Order;
import com.restaurant.restaurantorders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Order operations
 * Handles HTTP requests for order management
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class OrderController {

    private final OrderService orderService = new OrderService();

    /**
     * Create a new order
     * POST /api/v1/orders
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse order = orderService.createOrder(request);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order created successfully")
                .data(order)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get order by ID
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long orderId) {

        OrderResponse order = orderService.getOrderById(orderId);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get order status
     * GET /api/v1/orders/{orderId}/status
     */
    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<String>> getOrderStatus(
            @PathVariable Long orderId) {

        OrderResponse order = orderService.getOrderById(orderId);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .data(order.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders with pagination (Admin endpoint - should be in AdminController)
     * GET /api/v1/orders?page=0&size=20&sortBy=createdAt&sortDir=DESC
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String status) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderResponse> orders;
        if (status != null && !status.isEmpty()) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.getOrdersByStatus(orderStatus, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }

        ApiResponse<Page<OrderResponse>> response = ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update order status (Admin endpoint - should be in AdminController)
     * PUT /api/v1/orders/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.getStatus().toUpperCase());
        OrderResponse order = orderService.updateOrderStatus(orderId, newStatus);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order status updated successfully")
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get today's statistics (Admin endpoint - should be in AdminController)
     * GET /api/v1/orders/stats/today
     */
    @GetMapping("/stats/today")
    public ResponseEntity<ApiResponse<OrderService.TodayStats>> getTodayStats() {
        OrderService.TodayStats stats = orderService.getTodayStats();

        ApiResponse<OrderService.TodayStats> response = ApiResponse.<OrderService.TodayStats>builder()
                .success(true)
                .data(stats)
                .build();

        return ResponseEntity.ok(response);
    }
}
