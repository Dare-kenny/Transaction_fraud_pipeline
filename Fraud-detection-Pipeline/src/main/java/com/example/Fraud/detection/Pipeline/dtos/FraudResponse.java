package com.example.Fraud.detection.Pipeline.dtos;

import com.example.Fraud.detection.Pipeline.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FraudResponse { // dto for fraud context
    private Status status;
    private List<String> audits = new ArrayList<>(); // show the reason for disapproval
    private int score;
}
