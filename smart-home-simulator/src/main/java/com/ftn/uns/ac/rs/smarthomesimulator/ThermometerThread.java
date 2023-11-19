package com.ftn.uns.ac.rs.smarthomesimulator;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class ThermometerThread implements Runnable {

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
        int[][] dayStartEnd = new int[][] {{8,17}, {7,19}, {6,20}, {7,17}};

        //WIN,SPR,SUM,FAL {day,night}
        int[][][] typicalDayNightTemps = new int[][][] {{{0,10},{-5,5}}, {{15,25},{5,15}},
                                                        {{25,35},{15,25}}, {{15,25},{5,15}}};

        int[][] typicalDayNightHumidity = new int[][] {{40,50},{35,40},{30,35},{35,50}};
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
            int correctedTemp;
            if(now.getHour() >= currentDayStartEnd[0] && now.getHour() < currentDayStartEnd[1]) {
                if(now.getHour() > (currentDayStartEnd[1] -
                        ((currentDayStartEnd[1] - currentDayStartEnd[0]) / 2)))
                    correctedTemp = currentTypicalDayNightTemps[0][1] - 5;
                else
                    correctedTemp = currentTypicalDayNightTemps[0][0];
                System.out.println("Current temperature:" +
                        ThreadLocalRandom.current().nextInt(correctedTemp,
                                                      correctedTemp + 5) + "°C");
                System.out.println("Current humidity:" +
                        ThreadLocalRandom.current().nextInt(currentTypicalDayNightHumidity[0] - 3,
                                currentTypicalDayNightHumidity[0] + 4) + "%");
            }
            else {
                if(now.getHour() > (
                        (currentDayStartEnd[1] + ((currentDayStartEnd[1] - currentDayStartEnd[0]) / 2)) % 24))
                    correctedTemp = currentTypicalDayNightTemps[1][1] - 5;
                else
                    correctedTemp = currentTypicalDayNightTemps[1][0];

                System.out.println("Current temperature:" +
                        ThreadLocalRandom.current().nextInt(correctedTemp,
                                correctedTemp + 5) + "°C");
                System.out.println("Current humidity:" +
                        ThreadLocalRandom.current().nextInt(currentTypicalDayNightHumidity[1] - 3,
                                currentTypicalDayNightHumidity[1] + 4 ) + "%");
            }
            Thread.sleep(2000);
        }
    }

    public Thread getNewSimulatorThread() {
        Thread simulatorThread = new Thread(this);
        simulatorThread.start();
        return simulatorThread;
    }
}
