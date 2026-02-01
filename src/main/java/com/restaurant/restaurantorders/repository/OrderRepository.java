package com.restaurant.restaurantorders.repository;

import com.restaurant.restaurantorders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity
 * Provides database access methods for orders
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by status
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);

    /**
     * Find orders by customer phone
     */
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone);

    /**
     * Find orders created today
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startOfDay")
    List<Order> findTodayOrders(LocalDateTime startOfDay);

    /**
     * Count orders by status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Get total revenue for today
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :startOfDay AND o.status != 'CANCELLED'")
    Double getTodayRevenue(LocalDateTime startOfDay);

    /**
     * Find order with items (fetch join to avoid N+1 queries)
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Order findByIdWithItems(Long orderId);
}
