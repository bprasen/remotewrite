package com.javaclient.cortex;

import org.json.JSONObject;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        String filePath = "C:/DFolder/remotewrite/metricsInput.txt";
        String cortexURL = "http://localhost:9009/api/prom/push";

        FileReader fileReader = new FileReader();
        CortexPush cortexPush = new CortexPush();
        try {
            List<String> lines = fileReader.readFile(filePath);
            for (String jsonLine : lines) {
                JSONObject jsonObject = new JSONObject(jsonLine);
                if(jsonObject.has("jvm")){
                    String jvmMetrics = jsonObject.get("jvm").toString();
                    cortexPush.pushToCortex(jvmMetrics, cortexURL, "custom-service");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
