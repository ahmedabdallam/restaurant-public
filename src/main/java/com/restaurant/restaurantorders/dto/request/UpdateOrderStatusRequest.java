package com.restaurant.restaurantorders.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating order status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "^(PENDING|CONFIRMED|PREPARING|READY|OUT_FOR_DELIVERY|DELIVERED|CANCELLED)$",
        message = "Invalid status. Must be one of: PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED"
    )
    private String status;

    private String notes;
}
