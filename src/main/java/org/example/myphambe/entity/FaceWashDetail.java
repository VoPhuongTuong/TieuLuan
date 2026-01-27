package org.example.myphambe.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "facewash_details")
@Data
public class FaceWashDetail {

    @Id
    private Integer productId;

    private String skinType;
    private String foamType;
    private Boolean hasSalicylate;
    private String phLevel;
    private String volume;
    private String benefits;
    private String useInstructions;
    private String ingredients;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}
