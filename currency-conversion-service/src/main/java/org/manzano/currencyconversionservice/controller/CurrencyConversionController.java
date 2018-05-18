package org.manzano.currencyconversionservice.controller;

import org.manzano.currencyconversionservice.bean.CurrencyConversionBean;
import org.manzano.currencyconversionservice.feign.CurrencyExchangeServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    public static final String CURRENCY_EXCHANGE_API = "http://localhost:8000/currency-exchange/from/{from}/to/{to}";

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private Environment environment;

    @Autowired
    private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable("from") String from,
                                                  @PathVariable("to") String to,
                                                  @PathVariable("quantity") BigDecimal quantity) {

        CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(CURRENCY_EXCHANGE_API, CurrencyConversionBean.class, uriVariables);
        if (responseEntity.getBody() != null) {
            CurrencyConversionBean response = responseEntity.getBody();
            currencyConversionBean = new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()));
            currencyConversionBean.setPort(Integer.valueOf(environment.getProperty("server.port")));
        }

        return currencyConversionBean;
    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable("from") String from,
                                                       @PathVariable("to") String to,
                                                       @PathVariable("quantity") BigDecimal quantity) {

        CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean();
        CurrencyConversionBean response = currencyExchangeServiceProxy.retrieveExchangeValue(from, to);

        logger.info("{}", response);

        if (response != null) {
            currencyConversionBean = new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()));
//            currencyConversionBean.setPort(Integer.valueOf(environment.getProperty("server.port")));
            currencyConversionBean.setPort(response.getPort());
        }

        return currencyConversionBean;
    }

}
