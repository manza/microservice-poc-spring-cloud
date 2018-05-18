package org.manzano.currencyexchangeservice.controller;

import org.manzano.currencyexchangeservice.bean.ExchangeValue;
import org.manzano.currencyexchangeservice.repository.ExchangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchangeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private Environment environment;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public ExchangeValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {

        ExchangeValue exchangeValue = exchangeRepository.findByFromAndTo(from, to);

        if (exchangeValue != null) {
            exchangeValue.setPort(Integer.valueOf(environment.getProperty("server.port")));
        }

        logger.info("{}", exchangeValue);

        return exchangeValue;


    }
}
