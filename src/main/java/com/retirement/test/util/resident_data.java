package com.retirement.test.util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class resident_data {
        private Map<String, String> residentIP;
        private Map<String,String> residentSeverity;
        private Map<String,String> resident_remote;
        private Map<String,String> resident_health;
        private Map<String,String> resident_address;
        final String SERVER_ADDRESS = "localhost";
        final int PORT = 25550;


        public resident_data(){
            this.residentIP = readResidentData(1);
            System.out.println(residentIP.values().size());
            this.residentSeverity= readResidentData(5);
            this.resident_remote = readResidentData(4);
            this.resident_health = readResidentData(2);
            this.resident_address = readResidentData(3);
            System.out.println(residentSeverity.values().size());
            System.out.println(resident_remote.values().size());

            System.out.println(resident_health.values().size());
            System.out.println(resident_address.values().size());


        }


    private Map<String, String> readResidentData(int option) {
        Map<String, String> resident_data = new HashMap<>();
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + PORT);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeInt(option);
            objectOutputStream.flush();
            resident_data = (Map<String, String>) objectInputStream.readObject();
            if (resident_data == null)
            {
                resident_data = new HashMap<>();
            }
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        

        return resident_data;
    }

    public String getResidentIP(String unit){
        return this.residentIP.get(unit);
    }

    public String getResidentSeverity(String unit){
        return this.residentSeverity.get(unit);
    }

    public String getResidentUnitFromRemote(String unit){
        return this.resident_remote.get(unit);
    }
    public String getResidentHealth(String unit){
        return this.resident_health.get(unit);
    }

    public String getResidentAddress(String unit){
        return this.resident_address.get(unit);
    }

    public Map<String, String> getResidentIP(){
        return this.residentIP;
    }

    public boolean isResident(String unit){
        return resident_address.containsKey(unit);
    }

    public String getRemoteCodeForUnit(String unit){
        for (Map.Entry<String, String> entry : resident_remote.entrySet()) {
            if (entry.getValue().equals(unit)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void main(String [] args){
        resident_data data = new resident_data();
        System.out.println(data.getResidentIP("1"));
        System.out.println(data.getResidentSeverity("1"));
        System.out.println(data.getResidentUnitFromRemote("101"));
        System.out.println(data.getResidentHealth("1"));
        System.out.println(data.getResidentAddress("1"));
    }


}
