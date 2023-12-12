package com.ftn.uns.ac.rs.smarthomesockets.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    @MessageMapping("/control")
    public void recMessage(){
        System.out.println("lolll");
    }
}
