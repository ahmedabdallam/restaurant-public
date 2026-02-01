package com.restaurant.restaurantorders.controller;

import com.restaurant.restaurantorders.dto.response.ApiResponse;
import com.restaurant.restaurantorders.entity.Customer;
import com.restaurant.restaurantorders.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable UUID id) {
        return customerService.findById(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("CUSTOMER_NOT_FOUND", "Customer not found with id: " + id)));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByPhone(@PathVariable String phone) {
        return customerService.findByPhone(phone)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("CUSTOMER_NOT_FOUND", "Customer not found with phone: " + phone)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Customer created successfully", createdCustomer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALIDATION_ERROR", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updatedCustomer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALIDATION_ERROR", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable UUID id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("CUSTOMER_NOT_FOUND", e.getMessage()));
        }
    }
}
