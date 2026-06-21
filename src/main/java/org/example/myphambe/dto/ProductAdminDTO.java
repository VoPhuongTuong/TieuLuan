package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAdminDTO {
    private Integer id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private Integer year;
    private Integer stockQuantity;
    private Integer categoryId; 
    private String categoryName;

}