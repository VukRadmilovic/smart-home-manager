package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import org.eclipse.paho.mqttv5.common.MqttException;

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

        //WIN,SPR,SUM,FAL {day}
        int[][] typicalDayNightTemps = new int[][]{{0, 10}, {15, 25},
                {25, 35}, {15, 25}};
        int interval = 5;
        int[][] typicalDayNightHumidity = new int[][]{{40, 50}, {35, 40}, {30, 35}, {35, 50}};
        double tempValue, humValue;
        while (true) {
            int[] currentDayStartEnd;
            int[] currentTypicalDayNightTemps;
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
                tempValue = currentTypicalDayNightTemps[0] +
                        (((double) Math.abs(currentTypicalDayNightTemps[0] - currentTypicalDayNightTemps[1]) /
                                ((double) 60 / interval * 60 * Math.abs(currentDayStartEnd[1] - currentDayStartEnd[0]))) *
                                (((now.getHour() - currentDayStartEnd[0])) * 60 * 60 + 60 * now.getMinute() + now.getSecond()) / interval);
                humValue = currentTypicalDayNightHumidity[1] -
                        (((double) Math.abs(currentTypicalDayNightHumidity[1] - currentTypicalDayNightHumidity[0]) /
                                ((double) 60 / interval * 60 * Math.abs(currentDayStartEnd[1] - currentDayStartEnd[0]))) *
                                (((now.getHour() - currentDayStartEnd[0])) * 60 * 60 + 60 * now.getMinute() + now.getSecond()) / interval);

                sendAndDisplayMeasurements(tempValue, humValue);
            }
            else {
                int divisor = 24 - currentDayStartEnd[1] + now.getHour();
                int dividend = 24;
                if(divisor > 24) {
                    dividend = divisor;
                    divisor = 24;
                }
                double offsetTemp =  (((double) Math.abs(currentTypicalDayNightTemps[0] - currentTypicalDayNightTemps[1]) /
                        ((double) 60 / interval * 60 * Math.abs(24 - currentDayStartEnd[1] + currentDayStartEnd[0]))) *
                        (((dividend % divisor) * 60 * 60 + now.getMinute() * 60 + now.getSecond()) / interval));
                double offsetHum =  (((double) Math.abs(currentTypicalDayNightHumidity[1] - currentTypicalDayNightHumidity[0]) /
                        ((double) 60 / interval * 60 * Math.abs(currentDayStartEnd[1] - currentDayStartEnd[0]))) *
                        (((dividend % divisor) * 60 * 60 + now.getMinute() * 60 + now.getSecond()) / interval));
                tempValue = currentTypicalDayNightTemps[1] - offsetTemp;
                humValue = currentTypicalDayNightHumidity[0] + offsetHum;

                sendAndDisplayMeasurements(tempValue, humValue);
            }

            /*int correctedTemp;
            if (now.getHour() >= currentDayStartEnd[0] && now.getHour() < currentDayStartEnd[1]) {
                if (now.getHour() > (currentDayStartEnd[1] -
                        ((currentDayStartEnd[1] - currentDayStartEnd[0]) / 2)))
                    correctedTemp = currentTypicalDayNightTemps[0][1] - 5;
                else
                    correctedTemp = currentTypicalDayNightTemps[0][0];

                int temp = ThreadLocalRandom.current().nextInt(correctedTemp, correctedTemp + 5);
                int humidity = ThreadLocalRandom.current().nextInt(currentTypicalDayNightHumidity[0] - 3,
                        currentTypicalDayNightHumidity[0] + 4);

                sendAndDisplayMeasurements(temp, humidity);
            } else {
                if (now.getHour() > (
                        (currentDayStartEnd[1] + ((currentDayStartEnd[1] - currentDayStartEnd[0]) / 2)) % 24)) {
                    correctedTemp = currentTypicalDayNightTemps[1][1] - 5;
                }
                else
                    correctedTemp = currentTypicalDayNightTemps[1][0];

                int temp = ThreadLocalRandom.current().nextInt(correctedTemp, correctedTemp + 5);
                int humidity = ThreadLocalRandom.current().nextInt(currentTypicalDayNightHumidity[1] - 3,
                        currentTypicalDayNightHumidity[1] + 4);

                sendAndDisplayMeasurements(temp, humidity);
            }*/
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
