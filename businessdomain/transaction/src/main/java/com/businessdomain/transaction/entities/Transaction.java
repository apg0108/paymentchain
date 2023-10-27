package com.businessdomain.transaction.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "El campo account iban es requerido")
    @NotBlank(message = "El campo reference es requerido")
    @Column(unique = true)
    private String reference;
    @NotNull(message = "El campo account iban es requerido")
    @NotBlank(message = "El campo account iban es requerido")
    private String accountIban;
    private LocalDate date;
    @NotNull(message = "El campo amount es requerido")
    private double amount;
    private double fee;
    private String description;
    private Status status;
    private Channel channel;
}
