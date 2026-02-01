package com.restaurant.restaurantorders.repository;

import com.restaurant.restaurantorders.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MenuItem entity
 * Provides database access methods for menu items
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find all available menu items
     */
    List<MenuItem> findByIsAvailableTrueOrderByDisplayOrderAsc();

    /**
     * Find menu items by category
     */
    List<MenuItem> findByCategoryIdAndIsAvailableTrueOrderByDisplayOrderAsc(Long categoryId);

    /**
     * Find featured menu items
     */
    List<MenuItem> findByIsFeaturedTrueAndIsAvailableTrueOrderByDisplayOrderAsc();

    /**
     * Find menu items by category with join fetch
     */
    @Query("SELECT m FROM MenuItem m JOIN FETCH m.category WHERE m.category.id = :categoryId AND m.isAvailable = true ORDER BY m.displayOrder")
    List<MenuItem> findByCategoryIdWithCategory(Long categoryId);

    /**
     * Get all available menu items grouped by category
     */
    @Query("SELECT m FROM MenuItem m JOIN FETCH m.category c WHERE m.isAvailable = true AND c.isActive = true ORDER BY c.displayOrder, m.displayOrder")
    List<MenuItem> findAllAvailableWithCategory();
}
