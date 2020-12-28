package com.javaclient.cortex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public List<String> readFile(String filePath ) throws Exception{
        List<String> lines = new ArrayList<String>();
        java.io.FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new java.io.FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }catch (Exception ex){
            throw ex;
        }finally {
            if(bufferedReader != null) bufferedReader.close();
            if(fileReader != null) fileReader.close();
        }
        return lines;
    }
}
