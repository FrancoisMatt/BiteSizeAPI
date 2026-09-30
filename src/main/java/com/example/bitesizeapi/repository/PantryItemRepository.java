package com.example.bitesizeapi.repository;

import com.example.bitesizeapi.model.PantryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PantryItemRepository
        extends JpaRepository<PantryItem, Integer> {

    // Get all pantry items belonging to a user
    List<PantryItem> findByUserId(Integer userId);


    // Check whether a user already has an ingredient
    boolean existsByUserIdAndIngredientId(
            Integer userId,
            Integer ingredientId
    );


    // Check whether another pantry item already contains
    boolean existsByUserIdAndIngredientIdAndPantryItemIdNot(
            Integer userId,
            Integer ingredientId,
            Integer pantryItemId
    );
}