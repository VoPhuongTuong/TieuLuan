package org.example.myphambe.controller;

import org.example.myphambe.dto.CategoryDTO;
import org.example.myphambe.entity.Category;
import org.example.myphambe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories") // Khớp với file api.ts của bạn
@CrossOrigin(origins = "*")
public class CategoryAdminController {

    @Autowired
    private CategoryService categoryService;

    // Lấy danh sách kèm số lượng để hiện thị ở Dashboard/Categories Admin
    @GetMapping
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategoriesWithCount();
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Integer id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}