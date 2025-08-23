package com.example.Fraud.detection.Pipeline.services;

import com.example.Fraud.detection.Pipeline.dtos.FraudResponse;
import com.example.Fraud.detection.Pipeline.dtos.TransactionRequest;
import com.example.Fraud.detection.Pipeline.handlers.handlerStructure.HandlerStructure;
import com.example.Fraud.detection.Pipeline.model.FraudContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ChainService {

    @Autowired
    private HandlerStructure fraudChain;

    public ResponseEntity<?> checkChain(TransactionRequest transactionRequest){
        FraudContext context = new FraudContext();
        context = fraudChain.handleRequest(transactionRequest,context);
        return ResponseEntity.ok(
                FraudResponse.builder()
                        .status(context.getStatus())
                        .audits(context.getReasons())
                        .score(context.getScore())
                        .build()
        );
    }
}
