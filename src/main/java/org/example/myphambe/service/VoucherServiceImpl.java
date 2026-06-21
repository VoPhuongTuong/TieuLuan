package org.example.myphambe.service;

//import com.example.backend.entity.Voucher;
//import com.example.backend.mapper.VoucherMapper;
//import com.example.backend.repository.VoucherRepository;
//import com.example.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.VoucherDTO;
import org.example.myphambe.entity.Voucher;
import org.example.myphambe.mapper.VoucherMapper;
import org.example.myphambe.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    public VoucherDTO.Response create(VoucherDTO.CreateRequest request) {
        if (voucherRepository.existsByCode(request.getCode().toUpperCase().trim())) {
            throw new RuntimeException("Mã voucher '" + request.getCode() + "' đã tồn tại");
        }


        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }


        if ("PERCENT".equals(request.getDiscountType()) && request.getDiscountValue() > 100) {
            throw new RuntimeException("Giảm theo % không được vượt quá 100%");
        }

        Voucher voucher = voucherMapper.toEntity(request);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO.Response getById(Long id) {
        Voucher voucher = findById(id);
        return voucherMapper.toResponse(voucher);
    }

    @Override
    public VoucherDTO.Response getByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với mã: " + code));
        return voucherMapper.toResponse(voucher);
    }

    @Override
    public List<VoucherDTO.Response> getAll() {
        return voucherRepository.findAll()
                .stream()
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO.Response> getAllActive() {
        return voucherRepository.findAllValidVouchers(LocalDateTime.now())
                .stream()
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VoucherDTO.Response update(Long id, VoucherDTO.UpdateRequest request) {
        Voucher voucher = findById(id);

        if (request.getDiscountType() != null) {
            voucher.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            if ("PERCENT".equals(voucher.getDiscountType()) && request.getDiscountValue() > 100) {
                throw new RuntimeException("Giảm theo % không được vượt quá 100%");
            }
            voucher.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinOrderValue() != null) {
            voucher.setMinOrderValue(request.getMinOrderValue());
        }
        if (request.getQuantity() != null) {
            if (request.getQuantity() < voucher.getUsedCount()) {
                throw new RuntimeException("Số lượng mới không được nhỏ hơn số lượng đã dùng (" + voucher.getUsedCount() + ")");
            }
            voucher.setQuantity(request.getQuantity());
        }
        if (request.getStartDate() != null) {
            voucher.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            voucher.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            voucher.setStatus(request.getStatus());
        }

        if (voucher.getEndDate().isBefore(voucher.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public VoucherDTO.Response toggleStatus(Long id) {
        Voucher voucher = findById(id);
        voucher.setStatus("ACTIVE".equals(voucher.getStatus()) ? "INACTIVE" : "ACTIVE");
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Voucher voucher = findById(id);
        if (voucher.getUsedCount() > 0) {
            throw new RuntimeException("Không thể xóa voucher đã được sử dụng");
        }
        voucherRepository.delete(voucher);
    }

    @Override
    public VoucherDTO.ApplyResponse applyVoucher(VoucherDTO.ApplyRequest request) {
        Voucher voucher = voucherRepository
                .findValidVoucherByCode(request.getCode().toUpperCase().trim(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ, đã hết hạn hoặc hết lượt sử dụng"));

        double orderValue = request.getOrderValue();

        if (voucher.getMinOrderValue() != null && orderValue < voucher.getMinOrderValue()) {
            throw new RuntimeException(
                    String.format("Đơn hàng tối thiểu %.0f₫ để áp dụng voucher này", voucher.getMinOrderValue())
            );
        }

        double discountAmount;
        if ("PERCENT".equals(voucher.getDiscountType())) {
            discountAmount = orderValue * voucher.getDiscountValue() / 100;
        } else {
            discountAmount = Math.min(voucher.getDiscountValue(), orderValue);
        }

        double finalPrice = orderValue - discountAmount;

        return VoucherDTO.ApplyResponse.builder()
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .originalPrice(orderValue)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

    @Override
    @Transactional
    public void useVoucher(String code) {
        Voucher voucher = voucherRepository
                .findValidVoucherByCode(code.toUpperCase().trim(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ hoặc đã hết lượt"));

        voucher.setUsedCount(voucher.getUsedCount() + 1);

        if (voucher.getUsedCount() >= voucher.getQuantity()) {
            voucher.setStatus("INACTIVE");
        }

        voucherRepository.save(voucher);
    }

    private Voucher findById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với id: " + id));
    }
}