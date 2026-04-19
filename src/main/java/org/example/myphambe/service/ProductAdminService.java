package org.example.myphambe.service;

import org.example.myphambe.dto.ProductAdminDTO;
import java.util.List;

public interface ProductAdminService {
    List<ProductAdminDTO> getAllProductsForAdmin();
    ProductAdminDTO getProductDetail(Integer id);
    ProductAdminDTO createProduct(ProductAdminDTO dto);
    ProductAdminDTO updateProduct(Integer id, ProductAdminDTO dto);
    void deleteProduct(Integer id);
}