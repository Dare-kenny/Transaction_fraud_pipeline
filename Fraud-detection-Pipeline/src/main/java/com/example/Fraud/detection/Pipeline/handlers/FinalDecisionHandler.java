package com.example.Fraud.detection.Pipeline.handlers;

import com.example.Fraud.detection.Pipeline.dtos.TransactionRequest;
import com.example.Fraud.detection.Pipeline.enums.Status;
import com.example.Fraud.detection.Pipeline.handlers.handlerStructure.HandlerStructure;
import com.example.Fraud.detection.Pipeline.model.FraudContext;
import org.springframework.stereotype.Component;

@Component
public class FinalDecisionHandler implements HandlerStructure {


    @Override
    public void setNext(HandlerStructure nexthandler) {

    }

    @Override
    public FraudContext handleRequest(TransactionRequest transactionRequest , FraudContext context) {

        if (context.getStatus() == Status.REJECTED){
            return context;
        }

        if (context.getReasons() != null && !context.getReasons().isEmpty()){ //if the context reason isn't empty that means there's a slight issue
            context.setStatus(Status.REVIEWING); //flag as review
        }else {
            context.setStatus(Status.APPROVED); // or else that means it's appropriate
        }

        return context;
    }
}
