package org.example.myphambe.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.VoucherDTO;
import org.example.myphambe.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<VoucherDTO.Response> create(
            @Valid @RequestBody VoucherDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voucherService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<VoucherDTO.Response>> getAll() {

        return ResponseEntity.ok(voucherService.getAll());
    }


    @GetMapping("/active")
    public ResponseEntity<List<VoucherDTO.Response>> getAllActive() {
        return ResponseEntity.ok(voucherService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<VoucherDTO.Response> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.getByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody VoucherDTO.UpdateRequest request) {
        return ResponseEntity.ok(voucherService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<VoucherDTO.Response> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.toggleStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<VoucherDTO.ApplyResponse> apply(
            @Valid @RequestBody VoucherDTO.ApplyRequest request) {
        return ResponseEntity.ok(voucherService.applyVoucher(request));
    }

    @PatchMapping("/use/{code}")
    public ResponseEntity<Void> use(@PathVariable String code) {
        voucherService.useVoucher(code);
        return ResponseEntity.ok().build();
    }
}