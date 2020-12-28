package com.javaclient.cortex;

import com.github.wnameless.json.flattener.JsonFlattener;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.xerial.snappy.Snappy;
import prometheus.Remote;
import prometheus.Types;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CortexPush {

    private Types.TimeSeries.Builder timeSeriesBuilder = Types.TimeSeries.newBuilder();
    private Types.Sample.Builder sampleBuilder = Types.Sample.newBuilder();

    private Remote.WriteRequest.Builder writeRequestBuilder = Remote.WriteRequest.newBuilder();
    private final CloseableHttpClient httpClient = HttpClients.createSystem();

    public Map<String,Object> flattenJSONAsMap( String line ){
        return JsonFlattener.flattenAsMap(line);
    }

    public List<Types.TimeSeries> createTimeSeries(Map<String, Object> map, String appName){
        List<Types.TimeSeries> timeSeriesList = new ArrayList<>();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            timeSeriesBuilder.clear();
            sampleBuilder.clear();
            String name = entry.getKey().replace(".","_");
            String value = entry.getValue().toString();
            Types.Label metricNameLabel = Types.Label.newBuilder().setName("__name__").setValue(name).build();
            timeSeriesBuilder.addLabels(metricNameLabel);
            Types.Label appLabel = Types.Label.newBuilder().setName("app").setValue(appName).build();
            timeSeriesBuilder.addLabels(appLabel);
            sampleBuilder.setValue(Double.parseDouble(value));
            sampleBuilder.setTimestamp(System.currentTimeMillis());
            timeSeriesBuilder.addSamples(sampleBuilder.build());
            timeSeriesList.add(timeSeriesBuilder.build());
        }
        return timeSeriesList;
    }


    public void write( List<Types.TimeSeries> timeSeriesList, String url ) throws Exception{
        try{
            writeRequestBuilder.clear();
            Remote.WriteRequest writeRequest= writeRequestBuilder.addAllTimeseries(timeSeriesList).build();
            byte[] compressed = Snappy.compress(writeRequest.toByteArray());
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type","application/x-www-form-urlencoded");
            httpPost.setHeader("Content-Encoding", "snappy");
            httpPost.setHeader("X-Prometheus-Remote-Write-Version", "0.1.0");

            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(compressed);

            httpPost.getRequestLine();
            httpPost.setEntity(byteArrayEntity);
            httpClient.execute(httpPost);
        }catch(UnsupportedEncodingException uee){
            throw uee;
        }catch (IOException ioe){
            throw ioe;
        }catch (Exception ex) {
            throw ex;
        }
    }



    public void pushToCortex(String jsonString, String url, String appName) throws Exception{
        Map<String, Object> flattendMap = flattenJSONAsMap(jsonString);
        System.out.println("################### flattendMap : "+flattendMap);
        List<Types.TimeSeries> timeSeriesList = createTimeSeries(flattendMap, appName);
        System.out.println("################### timeSeriesList : "+timeSeriesList);
        //timeSeriesList.add(timeSeries);
        write(timeSeriesList, url);
    }
}
