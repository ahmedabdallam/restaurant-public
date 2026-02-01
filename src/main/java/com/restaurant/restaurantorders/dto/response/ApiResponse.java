package com.restaurant.restaurantorders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper
 * Used to standardize all API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Create success response with message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorDetails(code, message, null))
                .build();
    }

    /**
     * Create error response with details
     */
    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorDetails(code, message, details))
                .build();
    }

    /**
     * Error details nested class
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        public ErrorDetails(String code2, String message2, Object details2) {
			// TODO Auto-generated constructor stub
		}
		private String code;
        private String message;
        private Object details;
    }

	public static ApiResponse<OrderResponse> builder() {
		// TODO Auto-generated method stub
		return null;
	}
}
