package com.ftn.uns.ac.rs.smarthome;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PowerManager {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(PowerManager.class);
    private double powerConsumption = 0;
    private double powerProduction = 0;
    private double powerBalance = 0;

    public PowerManager() {
    }

    public void addConsumption(double consumption) {
        powerConsumption += consumption;
        powerBalance = powerProduction - powerConsumption;
        logger.info("Consumption: " + powerConsumption + " Production: " + powerProduction + " Balance: " + powerBalance);
    }

    public void addProduction(double production) {
        powerProduction += production;
        powerBalance = powerProduction - powerConsumption;
        logger.info("Consumption: " + powerConsumption + " Production: " + powerProduction + " Balance: " + powerBalance);
    }

    public double getPowerBalance() {
        return powerBalance;
    }

    public double getPowerConsumption() {
        return powerConsumption;
    }

    public double getPowerProduction() {
        return powerProduction;
    }

    public void reset() {
        powerConsumption = 0;
        powerProduction = 0;
        powerBalance = 0;
        logger.info("Consumption: " + powerConsumption + " Production: " + powerProduction + " Balance: " + powerBalance);
    }
}
