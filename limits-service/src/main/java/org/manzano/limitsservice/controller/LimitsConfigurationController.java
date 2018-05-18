package org.manzano.limitsservice.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.manzano.limitsservice.bean.LimitsConfiguration;
import org.manzano.limitsservice.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LimitsConfigurationController {

    @Autowired
    private Configuration configuration;

    @GetMapping("/limits")
    public LimitsConfiguration retrieveLimitsConfiguration() {
        return new LimitsConfiguration(configuration.getMinimum(), configuration.getMaximum());
    }

    /**
     * Simulation of Fault Tolerance mechanism in place
     * @return
     */
    @GetMapping("/limits-hystrics")
    @HystrixCommand(fallbackMethod = "fallbackRetrieveLimitsConfigurationHystrix")
    public LimitsConfiguration retrieveLimitsConfigurationHystrix() {
        throw new RuntimeException("error");
    }

    public LimitsConfiguration fallbackRetrieveLimitsConfigurationHystrix() {
        return new LimitsConfiguration(1, 999);
    }
}
