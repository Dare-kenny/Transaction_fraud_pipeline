package com.example.Fraud.detection.Pipeline.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "fraud")
@Data
public class FraudConfigProperties {

    private long amountThreshold;
    private List<String> geoAllowed;
    private List<String> blacklistCards;
    private int velocityWindowSeconds;
    private int maxAttempts;
}
