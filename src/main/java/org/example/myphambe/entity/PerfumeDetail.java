package org.example.myphambe.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "perfume_details")
@Data
public class PerfumeDetail {

    @Id
    private Integer productId;

    private String fragranceFamily;
    private String topNotes;
    private String middleNotes;
    private String baseNotes;
    private String longevity;
    private String suitableFor;
    private String volume;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}
