package com.ftn.uns.ac.rs.smarthome;

import com.ftn.uns.ac.rs.smarthome.services.InfluxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class PowerReportTask {
    private static final Logger log = LoggerFactory.getLogger(PowerReportTask.class);
    private final PowerManager powerManager;
    private final InfluxService influxService;
    private static final int FIXED_RATE = 15 * 1000;
    private static final int INITIAL_DELAY = 15 * 1000;

    public PowerReportTask(PowerManager powerManager,
                           InfluxService influxService) {
        this.powerManager = powerManager;
        this.influxService = influxService;
    }

    @Scheduled(initialDelay = INITIAL_DELAY, fixedRate = FIXED_RATE)
    public void runTask() {
        log.info("Power report task started");
        log.info("Consumption: " + powerManager.getPowerConsumption() + " Production: " + powerManager.getPowerProduction() + " Balance: " + powerManager.getPowerBalance());
        influxService.save("totalConsumption", String.valueOf(powerManager.getPowerConsumption()), new Date(),
                Map.of("unit", "kWh"));
        influxService.save("totalProduction", String.valueOf(powerManager.getPowerProduction()), new Date(),
                Map.of("unit", "kWh"));
        influxService.save("totalBalance", String.valueOf(powerManager.getPowerBalance()), new Date(),
                Map.of("unit", "kWh"));
        powerManager.reset();
    }
}
