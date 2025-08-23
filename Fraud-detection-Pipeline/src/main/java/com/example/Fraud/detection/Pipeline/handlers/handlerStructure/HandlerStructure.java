package com.example.Fraud.detection.Pipeline.handlers.handlerStructure;


import com.example.Fraud.detection.Pipeline.dtos.TransactionRequest;
import com.example.Fraud.detection.Pipeline.model.FraudContext;

public interface HandlerStructure{

    public void setNext(HandlerStructure nexthandler);
    public FraudContext handleRequest(TransactionRequest transactionRequest , FraudContext context);

}
