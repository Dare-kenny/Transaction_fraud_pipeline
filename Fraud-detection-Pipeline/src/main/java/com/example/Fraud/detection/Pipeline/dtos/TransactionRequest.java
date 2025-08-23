package com.example.Fraud.detection.Pipeline.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @NotBlank
    private String cardNumber;

    @Min(1)
    private long amount;

    @NotBlank
    private String country;

    @NotBlank
    private String ip;

    @NotBlank
    private String userId;

}
