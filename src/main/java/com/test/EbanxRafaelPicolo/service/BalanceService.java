package com.test.EbanxRafaelPicolo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.EbanxRafaelPicolo.model.Balance;
import com.test.EbanxRafaelPicolo.model.EventRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BalanceService {
    private final Map<Integer, Balance> balanceMap;

    public BalanceService(){
        balanceMap = new HashMap<>();
    }

    public void resetMap(){
        balanceMap.clear();
    }

    public String readEvent(EventRequest eventRequest){
        if(eventRequest.getType() == null){
            throw new IllegalArgumentException();
        }

        switch (eventRequest.getType()) {
            case "deposit" -> {
                return formatResponse(deposit(eventRequest));
            }
            case "withdraw" -> {
                return formatResponse(withdraw(eventRequest));
            }
            case "transfer" -> {
                return formatResponse(transfer(eventRequest));
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public String getBalance(int id){
        return balanceMap.getOrDefault(id, null).getBalance().toString();
    }


    private Map<String, Balance> withdraw(EventRequest eventRequest) {
        if(eventRequest.getOrigin() == null){
            throw new IllegalArgumentException();
        }

        int id = Integer.parseInt(eventRequest.getOrigin());

        if(!balanceMap.containsKey(id)){
            throw new IllegalArgumentException();
        }
        Balance balance = balanceMap.get(id);
        balance.withdraw(eventRequest.getAmount());
        return Map.of("origin", balance);
    }


    private Map<String, Balance> deposit(EventRequest eventRequest) {
        if(eventRequest.getDestination() == null){
            throw new IllegalArgumentException();
        }

        int id = Integer.parseInt(eventRequest.getDestination());

        if(!balanceMap.containsKey(id)){
            Balance balance = new Balance(id, eventRequest.getAmount());
            balanceMap.put(id, balance);
            return Map.of("destination", balance);
        }
        Balance balance = balanceMap.get(id);
        balance.deposit(eventRequest.getAmount());
        return Map.of("destination", balance);


    }

    private Map<String, Balance> transfer(EventRequest eventRequest) {
        if(eventRequest.getOrigin() == null || eventRequest.getDestination() == null){
            throw new IllegalArgumentException();
        }

        Map<String, Balance> originMap = new HashMap<>(withdraw(eventRequest));
        Map<String, Balance> destinationMap = deposit(eventRequest);

        originMap.putAll(destinationMap);

        return originMap;
    }

    private String formatResponse(Map<String, Balance> map){
        Map<String, Object> formattedResponse = new HashMap<>();

        for (Map.Entry<String, Balance> entry : map.entrySet()) {
            Balance balance = entry.getValue();
            Map<String, Object> balanceDetails = new HashMap<>();
            balanceDetails.put("id", String.valueOf(balance.getId()));
            balanceDetails.put("balance", balance.getBalance());
            formattedResponse.put(entry.getKey(), balanceDetails);
        }

        ObjectMapper mapper = new ObjectMapper();

        try{
            return mapper.writeValueAsString(formattedResponse);
        }catch (JsonProcessingException e){
            throw new IllegalArgumentException(e);
        }
    }
}
