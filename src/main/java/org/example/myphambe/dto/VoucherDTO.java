package org.example.myphambe.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class VoucherDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Mã voucher không được để trống")
        @Size(min = 3, max = 50, message = "Mã voucher từ 3 đến 50 ký tự")
        private String code;

        @NotBlank(message = "Loại giảm giá không được để trống")
        @Pattern(regexp = "PERCENT|FIXED", message = "Loại giảm giá phải là PERCENT hoặc FIXED")
        private String discountType;

        @NotNull(message = "Giá trị giảm không được để trống")
        @Positive(message = "Giá trị giảm phải lớn hơn 0")
        private Double discountValue;

        @PositiveOrZero(message = "Đơn tối thiểu không được âm")
        private Double minOrderValue;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotNull(message = "Ngày bắt đầu không được để trống")
        private LocalDateTime startDate;

        @NotNull(message = "Ngày kết thúc không được để trống")
        private LocalDateTime endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Pattern(regexp = "PERCENT|FIXED", message = "Loại giảm giá phải là PERCENT hoặc FIXED")
        private String discountType;

        @Positive(message = "Giá trị giảm phải lớn hơn 0")
        private Double discountValue;

        @PositiveOrZero(message = "Đơn tối thiểu không được âm")
        private Double minOrderValue;

        @Positive(message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        private LocalDateTime startDate;
        private LocalDateTime endDate;

        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Trạng thái phải là ACTIVE hoặc INACTIVE")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyRequest {

        @NotBlank(message = "Mã voucher không được để trống")
        private String code;

        @NotNull(message = "Giá trị đơn hàng không được để trống")
        @Positive(message = "Giá trị đơn hàng phải lớn hơn 0")
        private Double orderValue;
    }

    // ==================== RESPONSE ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private String discountType;
        private Double discountValue;
        private Double minOrderValue;
        private Integer quantity;
        private Integer usedCount;
        private Integer remaining;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyResponse {
        private String code;
        private String discountType;
        private Double discountValue;
        private Double originalPrice;
        private Double discountAmount;
        private Double finalPrice;
    }
}