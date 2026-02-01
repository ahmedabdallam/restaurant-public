package com.restaurant.restaurantorders.service;

import com.restaurant.restaurantorders.entity.Customer;
import com.restaurant.restaurantorders.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer findOrCreateCustomer(String name, String phone, String address, String email) {
        return customerRepository.findByPhone(phone)
                .map(existingCustomer -> {
                    if (name != null) existingCustomer.setName(name);
                    if (address != null) existingCustomer.setAddress(address);
                    if (email != null) existingCustomer.setEmail(email);
                    return customerRepository.save(existingCustomer);
                })
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(name);
                    newCustomer.setPhone(phone);
                    newCustomer.setAddress(address);
                    newCustomer.setEmail(email);
                    return customerRepository.save(newCustomer);
                });
    }

    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Customer with phone " + customer.getPhone() + " already exists");
        }
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(UUID id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        if (customerDetails.getName() != null) {
            customer.setName(customerDetails.getName());
        }
        if (customerDetails.getPhone() != null && !customerDetails.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhone(customerDetails.getPhone())) {
                throw new IllegalArgumentException("Phone number already in use");
            }
            customer.setPhone(customerDetails.getPhone());
        }
        if (customerDetails.getEmail() != null) {
            customer.setEmail(customerDetails.getEmail());
        }
        if (customerDetails.getAddress() != null) {
            customer.setAddress(customerDetails.getAddress());
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
