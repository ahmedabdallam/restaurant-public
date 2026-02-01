package com.restaurant.restaurantorders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String customerPhone;

    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotBlank(message = "Customer address is required")
    private String customerAddress;

    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    /**
     * DTO for order items in the request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "Menu item ID is required")
        @Positive(message = "Menu item ID must be positive")
        private Long menuItemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 99, message = "Quantity must not exceed 99")
        private Integer quantity;
    }
}
