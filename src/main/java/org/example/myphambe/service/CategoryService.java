package org.example.myphambe.service;

import org.example.myphambe.dto.CategoryDTO;
import org.example.myphambe.entity.Category;
import org.example.myphambe.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategoriesWithCount() {
        return categoryRepository.findAllWithProductCount();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục id: " + id));
        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        // Bạn có thể thêm logic kiểm tra: nếu productCount > 0 thì không cho xóa
        categoryRepository.deleteById(id);
    }
}