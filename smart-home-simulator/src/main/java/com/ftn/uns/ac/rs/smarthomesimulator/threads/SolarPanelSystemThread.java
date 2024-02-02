package com.ftn.uns.ac.rs.smarthomesimulator.threads;

import com.ftn.uns.ac.rs.smarthomesimulator.config.MqttConfiguration;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.SolarPanelSystem;
import com.ftn.uns.ac.rs.smarthomesimulator.services.ThreadMqttService;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SolarPanelSystemThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SolarPanelSystemThread.class);
    public static final int INTERVAL = 60;
    private final Integer deviceId;
    private final SolarPanelSystem system;
    private MqttConfiguration mqttConfiguration;
    private ThreadMqttService mqttService;
    private boolean isOff = false;
    private int count = 0;

    private static final double[] sunlightStarts = {6.5, 6.5, 6, 5.5, 5, 5, 5, 5.5, 6, 7, 7, 6.5};
    private static final double[] sunlightEnds = {16, 17, 17.5, 19, 20, 20, 20, 19.5, 19, 18, 17, 16};
    private static final double SUNLIGHT_PEAK = 12;
    private final double sunlightLengthToday;

    private final double sunlightWhichWillShineToday = calculateSunWhichWillShineToday();

    private double getSunlightIntensity() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int i = calendar.get(Calendar.MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        double distanceFromPeak = Math.abs(hour - SUNLIGHT_PEAK);
        double percentage = 1 - (distanceFromPeak / (SUNLIGHT_PEAK - sunlightStarts[i]));
        return Math.max(0, percentage) / sunlightWhichWillShineToday;
    }

    private double getSunlightIntensity(int month, int hour) {
        double distanceFromPeak = Math.abs(hour - SUNLIGHT_PEAK);
        double percentage = 1 - (distanceFromPeak / (SUNLIGHT_PEAK - sunlightStarts[month]));
        return Math.max(0, percentage);
    }

    private double calculateSunWhichWillShineToday() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int i = calendar.get(Calendar.MONTH);
        double power = 0;
        for (int j = 0; j < 24; j++) {
            power += getSunlightIntensity(i, j);
        }
        return power;
    }

    private class MqttSPSMessageCallback implements MqttCallback {
        @Override
        public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {}

        @Override
        public void mqttErrorOccurred(MqttException e) {System.err.println(e.getMessage());}

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) {
            try {
                String message = new String(mqttMessage.getPayload());
                if (message.equals("OFF")) {
                    isOff = true;
                } else if (message.equals("ON")) {
                    isOff = false;
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public void deliveryComplete(IMqttToken iMqttToken) {}

        @Override
        public void connectComplete(boolean b, String s) {}

        @Override
        public void authPacketArrived(int i, MqttProperties mqttProperties) {}
    }

    public SolarPanelSystemThread(SolarPanelSystem system) {
        try {
            this.mqttConfiguration = new MqttConfiguration(new MqttSPSMessageCallback());
            this.mqttService = new ThreadMqttService(this.mqttConfiguration);
        } catch (Exception e) {
            logger.error("Error while creating mqtt configuration: " + e.getMessage());
        }
        this.system = system;
        this.deviceId = system.getId();

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int i = calendar.get(Calendar.MONTH);
        sunlightLengthToday = sunlightEnds[i] - sunlightStarts[i];
    }

    @Override
    public void run() {
        try {
            mqttConfiguration.getClient().subscribe("commands/sps/" + system.getId(), 2);
            generatePower();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted: " + e.getMessage());
        } catch (MqttException ex) {
            logger.error("Error while subscribing to topic: " + "commands/sps/" + system.getId());
        }
    }

    public void generatePower() throws InterruptedException {
        while (true) {
            sendStatusUpdate();
            if (!isOff && count % 2 == 0) {
                count = 0;
                sendInternalState();
                // kWh = (number of panels * panel size * panel efficiency * (sunlight length today / 3 `peak sun hours only`)
                // amount of power which will be produced today
                double kWhProduced = system.getNumberOfPanels() * system.getPanelSize() * system.getPanelEfficiency() * (sunlightLengthToday / 3);
                // multiply by the percentage of today's power which will be generated during this hour
                kWhProduced *= getSunlightIntensity(); // amount of power which will be produced in this hour
                kWhProduced /= ((60.0 * 60) / INTERVAL); // amount of power which will be produced in this interval
                sendAndDisplayPower(kWhProduced);
            }

            Thread.sleep((INTERVAL / 2) * 1000);
            count++;
        }
    }

    private void sendStatusUpdate() {
        try {
            logger.info("Sending status message");
            this.mqttService.publishStatusMessageLite("status,1T," + deviceId);
        } catch (MqttException e) {
            logger.error("Error while publishing status message: " + e.getMessage());
        }
    }

    public void sendInternalState() {
        try {
            String status = isOff? "OFF" : "ON";
            String message = status + "," + deviceId;
            mqttService.publishOnOff(message, "sps");
            logger.info("Sending message: " + message);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void sendAndDisplayPower(double kWProduced) {
        DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
        df.setRoundingMode(RoundingMode.CEILING);
        String msg = "produced," + df.format(kWProduced) + "p," + deviceId + "," + system.getProperty().getId();

        logger.info("Sending message: " + msg);

        try {
            this.mqttService.publishMessageLite(msg,"measurements");
        } catch (MqttException e) {
            logger.error("Error while sending message: " + msg);
        }
    }

    public Thread getNewSimulatorThread() {
        Thread simulatorThread = new Thread(this);
        simulatorThread.start();
        return simulatorThread;
    }
}
