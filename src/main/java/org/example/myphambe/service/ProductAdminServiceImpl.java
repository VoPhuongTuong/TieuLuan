package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.ProductAdminDTO;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Category;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    @Override
    public List<ProductAdminDTO> getAllProductsForAdmin() {
        return productRepo.findAll().stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductAdminDTO> getAllProductsForAdminWithPagination(Pageable pageable) {
        return productRepo.findAll(pageable)
                .map(this::convertToAdminDTO);
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

    // Helper methods giữ nguyên
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