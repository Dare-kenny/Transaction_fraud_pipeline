package com.example.Fraud.detection.Pipeline.model;

import com.example.Fraud.detection.Pipeline.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudContext {
    private Status status;

    @Builder.Default
    private List<String> reasons = new ArrayList<>();

    @Builder.Default
    private int score = 0;
}
