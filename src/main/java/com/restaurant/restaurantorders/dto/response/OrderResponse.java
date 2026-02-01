package com.restaurant.restaurantorders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    /**
     * DTO for order item in response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal priceAtTime;
        private BigDecimal subtotal;
    }
}
