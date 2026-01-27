package org.example.myphambe.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "sunscreen_details")
@Data
public class SunscreenDetail {

    @Id
    private Integer productId;

    private String spf;
    private String paRating;
    private Boolean isWaterResistant;
    private String sunscreenType;
    private String skinType;
    private String volume;
    private String ingredients;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}
