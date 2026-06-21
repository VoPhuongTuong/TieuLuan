package org.example.myphambe.mapper;


import org.example.myphambe.dto.VoucherDTO;
import org.example.myphambe.entity.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public Voucher toEntity(VoucherDTO.CreateRequest request) {
        return Voucher.builder()
                .code(request.getCode().toUpperCase().trim())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0.0)
                .quantity(request.getQuantity())
                .usedCount(0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("ACTIVE")
                .build();
    }

    public VoucherDTO.Response toResponse(Voucher voucher) {
        return VoucherDTO.Response.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .quantity(voucher.getQuantity())
                .usedCount(voucher.getUsedCount())
                .remaining(voucher.getQuantity() - voucher.getUsedCount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .status(voucher.getStatus())
                .build();
    }
}