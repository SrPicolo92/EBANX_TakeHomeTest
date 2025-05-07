package com.test.EbanxRafaelPicolo.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testResetState() throws Exception {
        mockMvc.perform(post("/reset"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void testGetBalanceForNonExistingAccount() throws Exception {
        mockMvc.perform(get("/balance").param("account_id", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    @Order(3)
    public void testCreateAccountWithInitialBalance() throws Exception {
        String json = """
            {
              "type": "deposit",
              "destination": "100",
              "amount": 10
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(10));
    }

    @Test
    @Order(4)
    public void testDepositIntoExistingAccount() throws Exception {
        String json = """
            {
              "type": "deposit",
              "destination": "100",
              "amount": 10
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(20));
    }

    @Test
    @Order(5)
    public void testGetBalanceForExistingAccount() throws Exception {
        mockMvc.perform(get("/balance").param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    @Order(6)
    public void testWithdrawFromNonExistingAccount() throws Exception {
        String json = """
            {
              "type": "withdraw",
              "origin": "200",
              "amount": 10
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    @Order(7)
    public void testWithdrawFromExistingAccount() throws Exception {
        String json = """
            {
              "type": "withdraw",
              "origin": "100",
              "amount": 5
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(15));
    }

    @Test
    @Order(8)
    public void testTransferFromExistingAccount() throws Exception {
        String json = """
            {
              "type": "transfer",
              "origin": "100",
              "destination": "300",
              "amount": 15
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(0))
                .andExpect(jsonPath("$.destination.id").value("300"))
                .andExpect(jsonPath("$.destination.balance").value(15));
    }

    @Test
    @Order(9)
    public void testTransferFromNonExistingAccount() throws Exception {
        String json = """
            {
              "type": "transfer",
              "origin": "200",
              "destination": "300",
              "amount": 15
            }
            """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }
}
