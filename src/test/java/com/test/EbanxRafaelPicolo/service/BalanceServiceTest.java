package com.test.EbanxRafaelPicolo.service;

import com.test.EbanxRafaelPicolo.model.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceServiceTest {

    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceService = new BalanceService();
    }

    private void setField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void testResetMap() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "deposit");
        setField(request, "destination", "100");
        setField(request, "amount", new BigDecimal("10"));

        balanceService.readEvent(request);

        balanceService.resetMap();
        assertThrows(NullPointerException.class, () -> balanceService.getBalance(100));
    }

    @Test
    void testDepositCreatesAccount() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "deposit");
        setField(request, "destination", "100");
        setField(request, "amount", new BigDecimal("10"));

        String response = balanceService.readEvent(request);

        assertTrue(response.contains("\"id\":\"100\""));
        assertTrue(response.contains("\"balance\":10"));
    }

    @Test
    void testDepositToExistingAccount() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request1 = new EventRequest();
        setField(request1, "type", "deposit");
        setField(request1, "destination", "100");
        setField(request1, "amount", new BigDecimal("10"));

        String response1 = balanceService.readEvent(request1);
        assertTrue(response1.contains("\"balance\":10"));

        EventRequest request2 = new EventRequest();
        setField(request2, "type", "deposit");
        setField(request2, "destination", "100");
        setField(request2, "amount", new BigDecimal("5"));

        String response2 = balanceService.readEvent(request2);

        assertTrue(response2.contains("\"balance\":15"));
    }

    @Test
    void testWithdrawFromExistingAccount() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request1 = new EventRequest();
        setField(request1, "type", "deposit");
        setField(request1, "destination", "100");
        setField(request1, "amount", new BigDecimal("10"));

        String response1 = balanceService.readEvent(request1);
        assertTrue(response1.contains("\"balance\":10"));

        EventRequest request2 = new EventRequest();
        setField(request2, "type", "withdraw");
        setField(request2, "origin", "100");
        setField(request2, "amount", new BigDecimal("5"));

        String response2 = balanceService.readEvent(request2);

        assertTrue(response2.contains("\"balance\":5"));
    }

    @Test
    void testWithdrawFromNonExistentAccountThrows() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "withdraw");
        setField(request, "origin", "999");
        setField(request, "amount", new BigDecimal("10"));

        assertThrows(IllegalArgumentException.class, () -> balanceService.readEvent(request));
    }

    @Test
    void testTransferFromExistingToNewAccount() throws NoSuchFieldException, IllegalAccessException {
        EventRequest depositRequest = new EventRequest();
        setField(depositRequest, "type", "deposit");
        setField(depositRequest, "destination", "100");
        setField(depositRequest, "amount", new BigDecimal("15"));
        balanceService.readEvent(depositRequest);

        EventRequest transferRequest = new EventRequest();
        setField(transferRequest, "type", "transfer");
        setField(transferRequest, "origin", "100");
        setField(transferRequest, "destination", "200");
        setField(transferRequest, "amount", new BigDecimal("15"));

        String response = balanceService.readEvent(transferRequest);

        assertTrue(response.contains("\"origin\":{\"balance\":0"));
        assertTrue(response.contains("\"destination\":{\"balance\":15"));
    }

    @Test
    void testTransferFromNonExistentOrigin() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "transfer");
        setField(request, "origin", "999");
        setField(request, "destination", "200");
        setField(request, "amount", new BigDecimal("15"));

        assertThrows(IllegalArgumentException.class, () -> balanceService.readEvent(request));
    }

    @Test
    void testGetBalanceExistingAccount() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "deposit");
        setField(request, "destination", "100");
        setField(request, "amount", new BigDecimal("30"));
        balanceService.readEvent(request);

        String balance = balanceService.getBalance(100);
        assertEquals("30", balance);
    }

    @Test
    void testGetBalanceNonExistingAccountThrows() throws NoSuchFieldException, IllegalAccessException {
        assertThrows(NullPointerException.class, () -> balanceService.getBalance(999));
    }

    @Test
    void testReadEventWithInvalidTypeThrows() throws NoSuchFieldException, IllegalAccessException {
        EventRequest request = new EventRequest();
        setField(request, "type", "invalid");
        setField(request, "amount", new BigDecimal("0"));

        assertThrows(IllegalArgumentException.class, () -> balanceService.readEvent(request));
    }
}