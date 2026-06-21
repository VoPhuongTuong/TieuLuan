package org.example.myphambe.service;

import org.example.myphambe.dto.VoucherDTO;

import java.util.List;

public interface VoucherService {
    VoucherDTO.Response create(VoucherDTO.CreateRequest request);
    VoucherDTO.Response getById(Long id);
    VoucherDTO.Response getByCode(String code);
    List<VoucherDTO.Response> getAll();
    List<VoucherDTO.Response> getAllActive();
    VoucherDTO.Response update(Long id, VoucherDTO.UpdateRequest request);
    VoucherDTO.Response toggleStatus(Long id);
    void delete(Long id);
    VoucherDTO.ApplyResponse applyVoucher(VoucherDTO.ApplyRequest request);
    void useVoucher(String code);
}