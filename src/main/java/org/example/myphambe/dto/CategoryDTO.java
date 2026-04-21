package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name;
    private Long productCount; // Trường đếm số lượng sản phẩm
}