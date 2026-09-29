package com.example.bitesizeapi.repository;

import com.example.bitesizeapi.model.PantryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PantryItemRepository
        extends JpaRepository<PantryItem, Integer> {

    // Get pantry items belonging to a specific user
    List<PantryItem> findByUserId(Integer userId);
}