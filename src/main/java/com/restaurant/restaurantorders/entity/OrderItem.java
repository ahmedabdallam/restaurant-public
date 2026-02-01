package com.restaurant.restaurantorders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for order_items table
 * Represents individual items within an order
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Column(name = "menu_item_name", nullable = false, length = 200)
    private String menuItemName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "price_at_time", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper method to calculate subtotal
    public void calculateSubtotal() {
        this.subtotal = this.priceAtTime.multiply(BigDecimal.valueOf(this.quantity));
    }
}
