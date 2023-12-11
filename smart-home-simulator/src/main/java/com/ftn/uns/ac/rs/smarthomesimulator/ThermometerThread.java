package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.io.Console;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public class ThermometerThread implements Runnable {

    private final TemperatureUnit unit;
    private final MqttService mqttService;
    private final Integer deviceId;
    private int count = 1;

    public ThermometerThread(TemperatureUnit unit,
                             MqttService mqttService,
                             Integer deviceId) {
        this.unit = unit;
        this.mqttService = mqttService;
        this.deviceId = deviceId;
    }

    @Override
    public void run() {
        try {
            generateValues();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulator thread interrupted");
        }
    }

    public void generateValues() throws InterruptedException {

        String[] seasons = {
                "WIN", "WIN", "SPR", "SPR", "SPR", "SUM",
                "SUM", "SUM", "FAL", "FAL", "FAL", "WIN"
        };
        //WIN,SPR,SUM,FAL
        int[][] dayStartEnd = new int[][]{{8, 17}, {7, 19}, {6, 20}, {7, 17}};

        //WIN,SPR,SUM,FAL {day,night}
        int[][][] typicalDayNightTemps = new int[][][]{{{0, 10}, {-5, 0}}, {{15, 25}, {5, 15}},
                {{25, 35}, {15, 25}}, {{15, 25}, {5, 15}}};

        int interval = 5;
        int[][] typicalDayNightHumidity = new int[][]{{40, 50}, {35, 40}, {30, 35}, {35, 50}};
        double tempValue, humValue;
        while (true) {
            int[] currentDayStartEnd;
            int[][] currentTypicalDayNightTemps;
            int[] currentTypicalDayNightHumidity;
            int correctIndex;
            LocalDateTime now = LocalDateTime.now();
            String season = seasons[now.getMonthValue() - 1];
            correctIndex = switch (season) {
                case "WIN" -> 0;
                case "SPR" -> 1;
                case "SUM" -> 2;
                default -> 3;
            };
            currentDayStartEnd = dayStartEnd[correctIndex];
            currentTypicalDayNightTemps = typicalDayNightTemps[correctIndex];
            currentTypicalDayNightHumidity = typicalDayNightHumidity[correctIndex];
            if(now.getHour() >= currentDayStartEnd[0] && now.getHour() < currentDayStartEnd[1]) {
                int wholeSectionDuration = Math.abs(currentDayStartEnd[0] - currentDayStartEnd[1]);
                int multiplier = Math.abs(now.getHour() - currentDayStartEnd[0]);
                double hourAllowedTempDifference = (double) Math.abs(currentTypicalDayNightTemps[0][0] - currentTypicalDayNightTemps[0][1]) / (double) wholeSectionDuration;
                double hourAllowedHumDifference = (double) Math.abs(currentTypicalDayNightHumidity[0] - currentTypicalDayNightHumidity[1]) / (double) wholeSectionDuration;
                int halfDay = Math.abs(currentDayStartEnd[1] - currentDayStartEnd[0]) / 2;
                int multiplierHum = multiplier;
                if(now.getHour() >= halfDay) {
                    multiplier = multiplier % halfDay;
                }

                double maxTemp = hourAllowedTempDifference * multiplier + hourAllowedTempDifference;
                double minTemp = hourAllowedTempDifference * multiplier;
                double maxHum = hourAllowedHumDifference * multiplierHum + hourAllowedHumDifference;
                double minHum = hourAllowedHumDifference * multiplierHum;
                if(now.getHour() <= halfDay) {
                    tempValue = currentTypicalDayNightTemps[0][0] + (minTemp + Math.random() * (maxTemp - minTemp));
                }
                else {
                    tempValue = currentTypicalDayNightTemps[0][1] - (minTemp + Math.random() * (maxTemp - minTemp));
                }
                humValue = currentTypicalDayNightHumidity[1] - (minHum + Math.random() * (maxHum - minHum));
                sendAndDisplayMeasurements(tempValue, humValue);
            }
            else {
                int wholeSectionDuration = 24 - Math.abs(currentDayStartEnd[1] - currentDayStartEnd[0]);
                int divisor = 24 - currentDayStartEnd[1] + now.getHour();
                double hourAllowedTempDifference = (double) Math.abs(currentTypicalDayNightTemps[1][0] - currentTypicalDayNightTemps[1][1]) / (double) wholeSectionDuration;
                double hourAllowedHumDifference = (double) Math.abs(currentTypicalDayNightHumidity[0] - currentTypicalDayNightHumidity[1]) / (double) wholeSectionDuration;
                int halfNight = wholeSectionDuration / 2;
                int multiplier;
                boolean hasPassedHalf = false;
                if(divisor < 24) {
                    multiplier =  divisor;
                }
                else {
                    multiplier = divisor % 24;
                }
                int multiplierHum = multiplier;
                if(multiplier >= halfNight) {
                    hasPassedHalf = true;
                    multiplier = multiplier % halfNight;
                }
                double maxTemp = hourAllowedTempDifference * multiplier + hourAllowedTempDifference;
                double minTemp = hourAllowedTempDifference * multiplier;
                double maxHum = hourAllowedHumDifference * multiplierHum + hourAllowedHumDifference;
                double minHum = hourAllowedHumDifference * multiplierHum;
                if(!hasPassedHalf) {
                    tempValue = currentTypicalDayNightTemps[1][1] - (minTemp + Math.random() * (maxTemp - minTemp));
                }
                else {
                    tempValue = currentTypicalDayNightTemps[1][0] + (minTemp + Math.random() * (maxTemp - minTemp));
                }
                humValue = currentTypicalDayNightHumidity[0] + (minHum + Math.random() * (maxHum - minHum));
                sendAndDisplayMeasurements(tempValue, humValue);
            }

            Thread.sleep(interval * 1000);
        }
    }

    private void sendAndDisplayMeasurements(double temp, double humidity) {
        DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
        df.setRoundingMode(RoundingMode.CEILING);
        String msgHumidity = "humidity," + df.format(humidity) + "%," + deviceId;
        String msgTemp = "temperature," + df.format(temp) + "C," + deviceId;

        if (unit == TemperatureUnit.FAHRENHEIT) {
            temp =  temp * 1.8 + 32;
            msgTemp = "temperature," + df.format(temp) + "F," + deviceId;
        }

        System.out.println(msgTemp + "\n" + msgHumidity);

        try {
            this.mqttService.publishMeasurementMessageLite(msgTemp);
            this.mqttService.publishMeasurementMessageLite(msgHumidity);

            if (count % 2 == 0) {
                count = 1;
                System.out.println("Sending status message");
                this.mqttService.publishStatusMessageLite("status,1T," + deviceId);
            } else {
                count++;
            }
        } catch (MqttException e) {
            System.out.println("Error publishing message");
        }
    }

    public Thread getNewSimulatorThread() {
        Thread simulatorThread = new Thread(this);
        simulatorThread.start();
        return simulatorThread;
    }
}
