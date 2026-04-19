package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.OrderAdminResponseDTO;
import org.example.myphambe.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderAdminResponseDTO>> getOrders(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin(status, search));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderAdminResponseDTO> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(orderService.updateStatusByAdmin(id, status));
    }
}