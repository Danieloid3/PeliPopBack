package bd.pelipop.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "external_payment_id", nullable = false, length = 100)
    private String externalPaymentId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

}