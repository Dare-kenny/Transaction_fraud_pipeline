package com.example.Fraud.detection.Pipeline.controllers;

import com.example.Fraud.detection.Pipeline.dtos.TransactionRequest;
import com.example.Fraud.detection.Pipeline.services.ChainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudController {

    @Autowired
    private ChainService chainService;

    @PostMapping("/check")
    public ResponseEntity<?> check( @Valid @RequestBody TransactionRequest transactionRequest){
        return chainService.checkChain(transactionRequest);
    }
}
