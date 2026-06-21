package org.example.myphambe.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
    private Integer id;
    private String user;
    private String content;
    private Integer stars;
    private Integer productId;
}