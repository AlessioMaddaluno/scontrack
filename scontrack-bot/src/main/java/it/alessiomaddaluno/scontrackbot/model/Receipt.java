package it.alessiomaddaluno.scontrackbot.model;

import it.alessiomaddaluno.scontrackbot.enums.ReceiptType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "RECEPIT")
@Data
public class Receipt {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    @Column(name = "MERCHANT_NAME")
    private String merchantName;

    @Column(name = "TOTAL")
    private Float total;

    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Column(name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private ReceiptType category;

    @Column(name="BLOB_NAME", nullable = false)
    private String blobName;


}
