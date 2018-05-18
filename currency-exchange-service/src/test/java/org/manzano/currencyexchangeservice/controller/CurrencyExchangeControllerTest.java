package org.manzano.currencyexchangeservice.controller;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyExchangeControllerTest {

    private CurrencyExchangeController mockMvcController;

    public void ShouldReturnAGivenExchangeValueObject() throws Exception {
        Mockito.when(mockMvcController.retrieveExchangeValue("USD", "INR")).then()
        mockMvc.perform("/currency-exchange/from/{from}/to/{to}", "USD", "INR")
                .andExpect(status().isNotfoun)
    }

}