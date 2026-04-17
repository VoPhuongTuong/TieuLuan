package org.example.myphambe.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String user;
    private String content;
    private Integer stars;
}