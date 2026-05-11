package org.example.myphambe.service;

import org.example.myphambe.dto.ProductAdminDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductAdminService {
    List<ProductAdminDTO> getAllProductsForAdmin();
    Page<ProductAdminDTO> getAllProductsForAdminWithPagination(Pageable pageable);
    ProductAdminDTO getProductDetail(Integer id);
    ProductAdminDTO createProduct(ProductAdminDTO dto);
    ProductAdminDTO updateProduct(Integer id, ProductAdminDTO dto);
    void deleteProduct(Integer id);
}