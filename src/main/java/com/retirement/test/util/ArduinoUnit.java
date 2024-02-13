package com.retirement.test.util;

//package threads;

public class ArduinoUnit {
    private String arduinoId;
    private String ipAddress;

    public ArduinoUnit(String arduinoId, String ipAddress) {
        this.arduinoId = arduinoId;
        this.ipAddress = ipAddress;
    }

    public String getArduinoId() {
        return arduinoId;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}