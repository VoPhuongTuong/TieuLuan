package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.PageResponseDTO;
import org.example.myphambe.dto.ProductAdminDTO;
import org.example.myphambe.service.ProductAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductAdminController {

    private final ProductAdminService adminService;

    // Endpoint cũ - lấy tất cả (giữ để tương thích)
    @GetMapping("/all")
    public ResponseEntity<List<ProductAdminDTO>> getAll() {
        return ResponseEntity.ok(adminService.getAllProductsForAdmin());
    }

    // Endpoint mới - có phân trang
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductAdminDTO>> getAllWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductAdminDTO> productPage = adminService.getAllProductsForAdminWithPagination(pageable);

        PageResponseDTO<ProductAdminDTO> response = PageResponseDTO.<ProductAdminDTO>builder()
                .content(productPage.getContent())
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .first(productPage.isFirst())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductAdminDTO> create(@RequestBody ProductAdminDTO dto) {
        return new ResponseEntity<>(adminService.createProduct(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminDTO> update(@PathVariable Integer id, @RequestBody ProductAdminDTO dto) {
        return ResponseEntity.ok(adminService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}