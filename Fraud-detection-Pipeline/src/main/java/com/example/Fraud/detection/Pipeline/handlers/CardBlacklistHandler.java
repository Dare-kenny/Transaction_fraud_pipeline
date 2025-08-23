package com.example.Fraud.detection.Pipeline.handlers;

import com.example.Fraud.detection.Pipeline.configurations.FraudConfigProperties;

import com.example.Fraud.detection.Pipeline.dtos.TransactionRequest;
import com.example.Fraud.detection.Pipeline.enums.Status;
import com.example.Fraud.detection.Pipeline.handlers.handlerStructure.HandlerStructure;
import com.example.Fraud.detection.Pipeline.model.FraudContext;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CardBlacklistHandler implements HandlerStructure {

    private final FraudConfigProperties configProperties;
    private HandlerStructure nextHandler;

    String ErrorMessage = "blackListed card";

    @Override
    public void setNext(HandlerStructure nexthandler) { // set the amountThreshold handler as the next handler
        this.nextHandler = nexthandler;
    }

    private void addReason(FraudContext context ,String message ){
        if (context.getReasons() == null) {
            context.setReasons(new ArrayList<>());
        }
        context.getReasons().add(message);
    }

    @Override
    public FraudContext handleRequest(TransactionRequest transactionRequest , FraudContext context) {
        if(configProperties.getBlacklistCards() != null &&
                configProperties.getBlacklistCards().contains(transactionRequest.getCardNumber())){
            addReason(context,ErrorMessage);
            context.setScore(context.getScore()+100);
            return context;
        };

        return  (nextHandler!= null) ? nextHandler.handleRequest(transactionRequest , context) : context; // else return the context of the next handler
    }


}
