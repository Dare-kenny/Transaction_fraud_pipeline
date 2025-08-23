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
public class GeoIpHandler implements HandlerStructure {

    private final FraudConfigProperties configProperties;
    private HandlerStructure nextHandler;



    @Override
    public void setNext(HandlerStructure nexthandler) {
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

        if(configProperties != null && !configProperties.getGeoAllowed().contains(transactionRequest.getCountry())){
            String ErrorMessage = "Country:"+transactionRequest.getCountry()+" not in allowed set";
            addReason(context,ErrorMessage);
            context.setScore(context.getScore()+20);
            context.setStatus(Status.REVIEWING);
        }
        return  (nextHandler!= null) ? nextHandler.handleRequest(transactionRequest , context) : context;
    }
}
