package com.test.EbanxRafaelPicolo.controller;

import com.test.EbanxRafaelPicolo.model.EventRequest;
import com.test.EbanxRafaelPicolo.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/event")
    public ResponseEntity<String> readEvent(@RequestBody EventRequest request) {
        try {
            return ResponseEntity.status(201).body(balanceService.readEvent(request));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("0");
        }
    }


    @GetMapping("/balance")
    public ResponseEntity<String> getBalance(@RequestParam int account_id){
        try{
            return ResponseEntity.status(200).body(balanceService.getBalance(account_id));
        }catch (Exception e){
            return ResponseEntity.status(404).body("0");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetMap(){
        balanceService.resetMap();
        return ResponseEntity.ok("OK");
    }
}
