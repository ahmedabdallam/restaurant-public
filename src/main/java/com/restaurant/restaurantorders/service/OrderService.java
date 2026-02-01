package com.restaurant.restaurantorders.service;

import com.restaurant.restaurantorders.dto.request.CreateOrderRequest;
import com.restaurant.restaurantorders.dto.response.OrderResponse;
import com.restaurant.restaurantorders.entity.MenuItem;
import com.restaurant.restaurantorders.entity.Order;
import com.restaurant.restaurantorders.entity.OrderItem;
import com.restaurant.restaurantorders.exception.ResourceNotFoundException;
import com.restaurant.restaurantorders.repository.MenuItemRepository;
import com.restaurant.restaurantorders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Order operations
 * Handles business logic for order management
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Create a new order
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Create order entity
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerAddress(request.getCustomerAddress());
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);

        // Process order items
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item not found with id: " + itemRequest.getMenuItemId()));

            if (!menuItem.getIsAvailable()) {
                throw new IllegalStateException(
                        "Menu item is not available: " + menuItem.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtTime(menuItem.getPrice());
            orderItem.calculateSubtotal();

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        return mapToOrderResponse(order);
    }

    /**
     * Get all orders with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToOrderResponse);
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::mapToOrderResponse);
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    /**
     * Get today's statistics
     */
    @Transactional(readOnly = true)
    public TodayStats getTodayStats() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        List<Order> todayOrders = orderRepository.findTodayOrders(startOfDay);
        long pendingCount = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        Double revenue = orderRepository.getTodayRevenue(startOfDay);

        TodayStats stats = new TodayStats();
        stats.setTodayOrdersCount(todayOrders.size());
        stats.setPendingOrdersCount(pendingCount);
        stats.setTodayRevenue(revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO);

        return stats;
    }

    /**
     * Map Order entity to OrderResponse DTO
     */
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setCustomerAddress(order.getCustomerAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().toString());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());

        // Map order items if loaded
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                    .map(this::mapToOrderItemResponse)
                    .toList();
            response.setItems(items);
        }

        return response;
    }

    private OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(item.getId());
        response.setMenuItemName(item.getMenuItemName());
        response.setQuantity(item.getQuantity());
        response.setPriceAtTime(item.getPriceAtTime());
        response.setSubtotal(item.getSubtotal());
        return response;
    }

    /**
     * DTO for today's statistics
     */
    public static class TodayStats {
        private int todayOrdersCount;
        private long pendingOrdersCount;
        private BigDecimal todayRevenue;

        // Getters and setters
        public int getTodayOrdersCount() { return todayOrdersCount; }
        public void setTodayOrdersCount(int todayOrdersCount) { this.todayOrdersCount = todayOrdersCount; }
        public long getPendingOrdersCount() { return pendingOrdersCount; }
        public void setPendingOrdersCount(long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; }
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
    }
}
