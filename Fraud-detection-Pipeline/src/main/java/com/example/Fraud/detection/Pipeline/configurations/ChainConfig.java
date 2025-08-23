package com.example.Fraud.detection.Pipeline.configurations;

import com.example.Fraud.detection.Pipeline.handlers.AmountThresholdHandler;
import com.example.Fraud.detection.Pipeline.handlers.CardBlacklistHandler;
import com.example.Fraud.detection.Pipeline.handlers.FinalDecisionHandler;
import com.example.Fraud.detection.Pipeline.handlers.GeoIpHandler;
import com.example.Fraud.detection.Pipeline.handlers.handlerStructure.HandlerStructure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChainConfig {

    @Bean
    public HandlerStructure fraudChain(
            //cardblacklist -> amountThreshold -> geoIp -> finalDecision
            CardBlacklistHandler blacklistedCards,
            AmountThresholdHandler amount,
            GeoIpHandler geo,
            FinalDecisionHandler finalDecision
    ){
        blacklistedCards.setNext(amount);
        amount.setNext(geo);
        geo.setNext(finalDecision);

        return blacklistedCards;
    }
}
