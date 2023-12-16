package com.ftn.uns.ac.rs.smarthomesimulator.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ftn.uns.ac.rs.smarthomesimulator.config.MqttConfiguration;
import com.ftn.uns.ac.rs.smarthomesimulator.models.*;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.AirConditioner;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.ACMode;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.ACState;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.CommandType;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ACThread implements Runnable {

    private double currentTemp;
    private final AirConditioner ac;
    private final ObjectMapper mapper = new ObjectMapper();
    private ACCommand settings;
    private boolean isOff = true;
    private MqttConfiguration mqttConfiguration;
    private boolean hasConfigChanged = true;

    private final Map<Long, ScheduledFuture<?>> scheduledThread = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler =  Executors.newScheduledThreadPool(5);
    private final AtomicLong scheduledThreadCount = new AtomicLong(0);

    private class MqttACMessageCallback implements MqttCallback {

        @Override
        public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {}

        @Override
        public void mqttErrorOccurred(MqttException e) {System.err.println(e.getMessage());}

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) {

            try {
                hasConfigChanged = true;
                String message = new String(mqttMessage.getPayload());
                ACCommand receivedCommand = mapper.readValue(message, ACCommand.class);
                CommandType commandType = receivedCommand.getCommandType();
                if(commandType == CommandType.ON || commandType == CommandType.CHANGE) {
                    if(commandType == CommandType.CHANGE) {
                        publishChanges(receivedCommand);
                    }
                    else {
                        publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                                ACState.ON.toString(),null));
                    }
                    isOff = false;
                    settings = receivedCommand;
                }
                else if(commandType == CommandType.OFF){
                    publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                            ACState.OFF.toString(),null));
                    isOff = true;
                }
                else if(commandType == CommandType.CANCEL_SCHEDULE) {
                    removeScheduledThread(receivedCommand);
                }
                else {
                    Map<String,String> extraInfo = new HashMap<>();
                    extraInfo.put("from",receivedCommand.getCommandParams().getFrom().toString());
                    extraInfo.put("to",receivedCommand.getCommandParams().getTo().toString());
                    extraInfo.put("everyDay", String.valueOf(receivedCommand.getCommandParams().isEveryDay()));
                    publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                            ACState.SCHEDULE.toString(),extraInfo));
                    scheduleThread(receivedCommand);
                }
            }
            catch(Exception ex) {
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
    public ACThread(AirConditioner ac,
                    ACCommand settings) {
        try {
            this.mqttConfiguration = new MqttConfiguration(new MqttACMessageCallback());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.ac = ac;
        this.settings = settings;
        currentTemp = 0;
    }

    @Override
    public void run() {
        try {
            mqttConfiguration.getClient().subscribe("command/ac/" + ac.getId(), 2);
            generateValues();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulator thread interrupted");
        }
        catch(MqttException ex) {
            System.err.println("MQTT Error: " + ex.getMessage());
        }
    }
    public void generateValues() throws InterruptedException {
        int interval = 5;
        String[] seasons = {
                "WIN", "WIN", "SPR", "SPR", "SPR", "SUM",
                "SUM", "SUM", "FAL", "FAL", "FAL", "WIN"
        };

        //WIN,SPR,SUM,FAL
        int[][] typicalTempRange = new int[][]{{16, 20}, {18, 23}, {21, 27}, {19, 24}};
        while(true) {
            sendInternalState();
            LocalDateTime now = LocalDateTime.now();
            String season = seasons[now.getMonthValue() - 1];
            int correctIndex = switch (season) {
                case "WIN" -> 0;
                case "SPR" -> 1;
                case "SUM" -> 2;
                default -> 3;
            };
            if (settings != null) {
                if(hasConfigChanged) {
                    if (settings.getCommandParams().getCurrentTemp() == -1) {
                        currentTemp = ThreadLocalRandom.current().nextInt(typicalTempRange[correctIndex][0], typicalTempRange[correctIndex][1] + 1);
                        if(settings.getCommandParams().getUnit().equals("F"))
                            currentTemp = (currentTemp * 9/5) + 32;
                    } else
                        currentTemp = settings.getCommandParams().getCurrentTemp();

                }
                ACValueDigest value;
                if (!isOff) {
                    value = generateValue(settings.getCommandParams().getTarget(),
                            settings.getCommandParams().getFanSpeed(),
                            settings.getCommandParams().getMode(),
                            settings.getCommandParams().isHealth(),
                            settings.getCommandParams().isFungus());
                    sendStateAndStatus(value);
                }
                hasConfigChanged = false;
            }
            Thread.sleep(interval * 1000);
        }

    }
    public Thread getNewSimulatorThread() {
        Thread simulatorThread = new Thread(this);
        simulatorThread.start();
        return simulatorThread;
    }


    private ACValueDigest generateValue(int targetTemp, int fanSpeed,
                                       ACMode mode, boolean isIonizing,
                                       boolean preventFungus) {
        boolean isCelsius = settings.getCommandParams().getUnit().equals("C");
        double tempChangePerInterval = isCelsius ? fanSpeed * 0.04 : fanSpeed * 0.15;
        if(mode == ACMode.HEAT) {
            if (currentTemp < targetTemp) {
                currentTemp += tempChangePerInterval;
            }
            if(currentTemp > targetTemp) {
               currentTemp = targetTemp;
            }
        }
        if(mode == ACMode.COOL) {
            if (currentTemp > targetTemp)
                currentTemp -= tempChangePerInterval;
            if(currentTemp < targetTemp) {
                currentTemp = targetTemp;
            }
        }
        if(mode == ACMode.AUTO) {
            if(currentTemp < targetTemp) {
                currentTemp += tempChangePerInterval;
                if(currentTemp > targetTemp)
                    currentTemp = targetTemp;
            }
            if (currentTemp > targetTemp) {
                currentTemp -= tempChangePerInterval;
                if(currentTemp < targetTemp)
                    currentTemp = targetTemp;
            }
        }
        //currentTemp -= (0.01 + Math.random() * (0.02 - 0.01)); //random temp dropout
        System.out.println(currentTemp);
        return new ACValueDigest(ac.getId(),
                currentTemp,
                settings.getCommandParams().getTarget(),
                ac.getTemperatureUnit() == TemperatureUnit.CELSIUS? "C" : "F",
                mode.toString(),fanSpeed,isIonizing,preventFungus);
    }

    public void sendStateAndStatus(ACValueDigest state) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(state);
            int onOff = isOff ? 3 : 2;
            publishMessageLite(json);
            publishStatusMessageLite("status," + onOff + "T," + ac.getId());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void sendInternalState() {
        try {
            String status = isOff? "OFF" : "ON";
            publishOnOff(status + "," + ac.getId());
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void scheduleThread(ACCommand received) {
        long from = received.getCommandParams().getFrom();
        long to = received.getCommandParams().getTo();
        long init, stopTime;
        long nowMillis = new Date().getTime();
        if (from < nowMillis) {
            init = (from + 1000 * 60 * 60 * 24) - nowMillis;
            stopTime = (to + 1000 * 60 * 60 * 24) - nowMillis;
        } else {
            init = from - nowMillis;
            stopTime = to - nowMillis;
        }
        Runnable changeSettings = () -> {
            settings = received;
            isOff = false;
        };
        if (received.getCommandParams().isEveryDay()) {
            ScheduledFuture<?> everyDay = scheduler.scheduleWithFixedDelay(() -> {
                scheduler.schedule(
                        changeSettings,
                        0,
                        TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> {
                    isOff = true;
                }, stopTime, TimeUnit.MILLISECONDS);
            }, init, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
            scheduledThread.put(scheduledThreadCount.incrementAndGet(), everyDay);
        } else {
            ScheduledFuture<?> once = scheduler.schedule(() -> {
                scheduler.schedule(
                        changeSettings,
                        init,
                        TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> {
                    isOff = true;
                }, stopTime, TimeUnit.MILLISECONDS);
            },0, TimeUnit.MILLISECONDS);
            scheduledThread.put(scheduledThreadCount.incrementAndGet(), once);
        }
    }

    private void removeScheduledThread(ACCommand received) {
        Long scheduledTaskId = received.getCommandParams().getTaskId();
        if(scheduledThread.containsKey(scheduledTaskId)) {
            scheduledThread.get(scheduledTaskId).cancel(true);
            scheduledThread.remove(scheduledTaskId);
        }
    }

    private void publishMessageLite(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("ac", new MqttMessage(message.getBytes()));
    }

    private void publishOnOff(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("status/ac", new MqttMessage(message.getBytes()));
    }

    private void publishStatusMessageLite(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("statuses", new MqttMessage(message.getBytes()));
    }

    private void publishStateMessage(ACStateChange state) {
        try {
            this.mqttConfiguration.getClient().publish("states", new MqttMessage(mapper.writeValueAsString(state).getBytes()));
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    private void publishChanges(ACCommand receivedCommand) {
        int changedTemp, changedFanSpeed;
        if(receivedCommand.getCommandParams().isHealth() != settings.getCommandParams().isHealth()) {
            Map<String,String> extraInfo = new HashMap<>();
            extraInfo.put("isHealth",String.valueOf(receivedCommand.getCommandParams().isHealth()));
            publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                    ACState.HEALTH_CHANGE.toString(),extraInfo));
        }
        if(receivedCommand.getCommandParams().isFungus() != settings.getCommandParams().isFungus()) {
            Map<String,String> extraInfo = new HashMap<>();
            extraInfo.put("isFungus",String.valueOf(receivedCommand.getCommandParams().isFungus()));
            publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                    ACState.FUNGUS_CHANGE.toString(),extraInfo));
        }
        if(receivedCommand.getCommandParams().getMode() != settings.getCommandParams().getMode()) {
            if (receivedCommand.getCommandParams().getMode() == ACMode.AUTO) {
                publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                        ACState.AUTO_MODE.toString(),null));
            }
            if (receivedCommand.getCommandParams().getMode() == ACMode.COOL) {
                publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                        ACState.COOL_MODE.toString(),null));
            }
            if(receivedCommand.getCommandParams().getMode() == ACMode.HEAT) {
                publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                        ACState.HEAT_MODE.toString(),null));
            }
            if(receivedCommand.getCommandParams().getMode() == ACMode.DRY) {
                publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                        ACState.DRY_MODE.toString(),null));
            }
        }
        if(!Objects.equals(receivedCommand.getCommandParams().getTarget(), settings.getCommandParams().getTarget())) {
            changedTemp = receivedCommand.getCommandParams().getTarget();
            Map<String,String> extraInfo = new HashMap<>();
            extraInfo.put("target",String.valueOf(changedTemp));
            publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                    ACState.TEMP_CHANGE.toString(),extraInfo));
        }
        if(!Objects.equals(receivedCommand.getCommandParams().getFanSpeed(), settings.getCommandParams().getFanSpeed())) {
            changedFanSpeed = receivedCommand.getCommandParams().getFanSpeed();
            Map<String,String> extraInfo = new HashMap<>();
            extraInfo.put("fanSpeed",String.valueOf(changedFanSpeed));
            publishStateMessage(new ACStateChange(receivedCommand.getCommandParams().getUserId(),
                    ACState.FAN_SPEED_CHANGE.toString(),extraInfo));
        }
    }

}
