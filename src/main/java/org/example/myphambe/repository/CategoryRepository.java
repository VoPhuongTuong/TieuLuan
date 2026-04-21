package org.example.myphambe.repository;

import org.example.myphambe.dto.CategoryDTO;
import org.example.myphambe.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT new org.example.myphambe.dto.CategoryDTO(c.id, c.name, COUNT(p)) " +
            "FROM Category c " +
            "LEFT JOIN Product p ON c.id = p.category.id " +
            "GROUP BY c.id, c.name")
    List<CategoryDTO> findAllWithProductCount();
}