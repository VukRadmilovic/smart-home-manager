package com.ftn.uns.ac.rs.smarthome;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.Battery;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.InfluxService;
import com.ftn.uns.ac.rs.smarthome.services.MqttService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IBatteryService;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PowerReportTask {
    private static final Logger log = LoggerFactory.getLogger(PowerReportTask.class);
    private final PowerManager powerManager;
    private final InfluxService influxService;
    private final IBatteryService batteryService;
    private final MqttService mqttService;
    private final PropertyRepository propertyRepository;
    private static final int FIXED_RATE = 60 * 1000;
    private static final int INITIAL_DELAY = 60 * 1000;

    public PowerReportTask(PowerManager powerManager,
                           InfluxService influxService,
                           IBatteryService batteryService,
                           MqttService mqttService,
                           PropertyRepository propertyRepository) {
        this.powerManager = powerManager;
        this.influxService = influxService;
        this.batteryService = batteryService;
        this.mqttService = mqttService;
        this.propertyRepository = propertyRepository;
    }

    @Scheduled(initialDelay = INITIAL_DELAY, fixedRate = FIXED_RATE)
    public void runTask() {
        log.info("Power report task started");
        List<Integer> properties = propertyRepository.findAll().stream().map(Property::getId).toList();
        for (int propertyId: properties) {
            log.info("Property " + propertyId + " power report");
            log.info("Consumption: " + powerManager.getPowerConsumption(propertyId) + " Production: " + powerManager.getPowerProduction(propertyId) + " Balance: " + powerManager.getPowerBalance(propertyId));

            double originalPowerBalance = powerManager.getPowerBalance(propertyId);
            double powerBalance = originalPowerBalance;
            if (originalPowerBalance > 0) {
                log.info("Power balance is positive.");
                List<Battery> batteries = batteryService.getAllNonFull();
                if (batteries.isEmpty()) {
                    log.info("No batteries found to distribute power to. Sending power to grid.");
                } else {
                    log.info("Attempting to distribute power to batteries.");
                    double totalRemainingCapacity = batteries.stream().mapToDouble(Battery::getRemainingCapacity).sum();

                    for (Battery battery : batteries) {
                        double powerToDistribute = originalPowerBalance * (battery.getRemainingCapacity() / totalRemainingCapacity);
                        // store as much as possible
                        double powerToStore = Math.min(powerToDistribute, battery.getCapacity() - battery.getOccupiedCapacity());
                        battery.setOccupiedCapacity(battery.getOccupiedCapacity() + powerToStore); // store power
                        powerBalance -= powerToStore;
                        if (powerToStore < powerToDistribute) {
                            log.info("Battery " + battery.getId() + " is FULL, storing " + powerToStore + " kWh");
                        } else {
                            log.info("Battery " + battery.getId() + " stored " + powerToStore + " kWh");
                        }
                        batteryService.update(battery);
                    }

                    if (powerBalance < 0.001) {
                        powerBalance = 0;
                    }
                    log.info("Power balance before distribution: " + originalPowerBalance);
                    log.info("Power stored in batteries: " + (originalPowerBalance - powerBalance));
                    log.info("Power balance after distribution: " + powerBalance + ".");
                    if (powerBalance > 0) {
                        log.info("Power balance is still positive, sending remaining power to grid.");
                    }
                }
            } else if (originalPowerBalance == 0) {
                log.info("Power balance is perfectly balanced.");
            } else {
                log.info("Power balance is negative, taking power from grid");
            }
            influxService.save("totalConsumption", powerManager.getPowerConsumption(propertyId), new Date(),
                    Map.of("unit", "kWh", "deviceId", "-1", "propertyId", String.valueOf(propertyId)));
            influxService.save("totalProduction", powerManager.getPowerProduction(propertyId), new Date(),
                    Map.of("unit", "kWh", "propertyId", String.valueOf(propertyId)));
            influxService.save("totalBalance", powerManager.getPowerBalance(propertyId), new Date(),
                    Map.of("unit", "kWh", "propertyId", String.valueOf(propertyId)));
            influxService.save("totalStored", originalPowerBalance - powerBalance, new Date(),
                    Map.of("unit", "kWh", "propertyId", String.valueOf(propertyId)));
            influxService.save("totalSentToGrid", powerBalance, new Date(),
                    Map.of("unit", "kWh", "propertyId", String.valueOf(propertyId)));
            try {
                DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
                df.setRoundingMode(RoundingMode.CEILING);
                String msg = "totalConsumption," + df.format(powerManager.getPowerConsumption(propertyId)) + "p," + propertyId;
                log.info("Publishing power message: " + msg);
                this.mqttService.publishPowerMessage(msg);
            } catch (MqttException ex) {
                log.error("Error publishing power message: " + ex.getMessage());
            }
            log.info("Power report task finished for property " + propertyId + ".\n");
        }

        powerManager.reset();
    }
}
