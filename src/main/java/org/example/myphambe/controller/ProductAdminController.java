package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.ProductAdminDTO;
import org.example.myphambe.service.ProductAdminService;
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

    @GetMapping
    public ResponseEntity<List<ProductAdminDTO>> getAll() {
        return ResponseEntity.ok(adminService.getAllProductsForAdmin());
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