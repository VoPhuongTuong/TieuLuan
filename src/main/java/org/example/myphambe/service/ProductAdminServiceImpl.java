package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.ProductAdminDTO;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Category;
import org.example.myphambe.repository.ProductAdminRepository;
import org.example.myphambe.repository.CategoryRepository;
import org.example.myphambe.service.ProductAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

    private final ProductAdminRepository productRepo;
    private final CategoryRepository categoryRepo;

    @Override
    public List<ProductAdminDTO> getAllProductsForAdmin() {
        return productRepo.findAll().stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductAdminDTO createProduct(ProductAdminDTO dto) {
        Product product = new Product();
        mapDtoToEntity(dto, product);
        Product savedProduct = productRepo.save(product);
        return convertToAdminDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductAdminDTO updateProduct(Integer id, ProductAdminDTO dto) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));
        
        mapDtoToEntity(dto, existingProduct);
        return convertToAdminDTO(productRepo.save(existingProduct));
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepo.deleteById(id);
    }

    @Override
    public ProductAdminDTO getProductDetail(Integer id) {
        return productRepo.findById(id)
                .map(this::convertToAdminDTO)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Helper: Map từ Entity sang Admin DTO
    private ProductAdminDTO convertToAdminDTO(Product entity) {
        return ProductAdminDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brand(entity.getBrand())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .description(entity.getDescription())
                .year(entity.getYear())
                .stockQuantity(entity.getStockQuantity())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "Không có danh mục")
                .build();
    }

    // Helper: Map từ DTO sang Entity để lưu DB
    private void mapDtoToEntity(ProductAdminDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setBrand(dto.getBrand());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setDescription(dto.getDescription());
        entity.setYear(dto.getYear());
        entity.setStockQuantity(dto.getStockQuantity());
        
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category không tồn tại"));
            entity.setCategory(cat);
        }
    }
}