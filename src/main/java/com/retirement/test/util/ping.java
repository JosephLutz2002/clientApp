package com.retirement.test.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class ping implements Runnable {

    private static final String ARDUINO_LIST_FILE = "list.txt";
    private static String LOG_FILE = "data/logs.txt";
    private Map<String,String> ip_addresses;
    private List<ArduinoUnit> arduinoUnits;
    private final Consumer<String> logAppender;

    public ping(Consumer<String> logAppender, Map<String,String> ip_addresses) {
     this.logAppender = logAppender;
     this.ip_addresses = ip_addresses;
    }

    @Override
    public void run() {
        try {
            //loadArduinoUnits();
            while (true) {
                System.out.println("Client emergency listening");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LOG_FILE = "logs/"+dateFormat.format(new Date()) + ".log";
                 for (Map.Entry<String, String> entry : ip_addresses.entrySet()) {
                    System.out.println(entry.getValue());
                    boolean isReachable = ping(entry.getValue());
                    if (!isReachable) {
                        log("Arduino unit " + entry.getKey() + " at " + entry.getValue() + " did not respond.\n");
                        logAppender.accept(entry.getKey());
                }else{
                    log("Arduino unit " + entry.getKey() + " at " + entry.getValue() + " did respond.\n");

                }
        }
                Thread.sleep(3600000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean ping(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            return address.isReachable(2000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void log(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

