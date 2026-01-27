package org.example.myphambe.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private LocalDateTime orderDate;

    private BigDecimal totalPrice;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
