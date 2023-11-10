package com.ftn.uns.ac.rs.smarthomesimulator.config;

import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling initial setup of the system (getting unique pub/sub channels from other servers)
 */
@RestController
@RequestMapping("/api/handshake")
public class HandshakeController {

    private final MqttService mqttService;

    public HandshakeController(MqttService mqttService) {
        this.mqttService = mqttService;
    }


    @PostMapping(value = "/newServer/{uuid}", consumes = "application/json")
    public ResponseEntity<?> registerNewServer(@PathVariable long uuid) {
        try{
            this.mqttService.registerNewServerTopics(uuid);
            return new ResponseEntity<>("Server successfully registered.", HttpStatus.OK);
        }
        catch(MqttException ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
