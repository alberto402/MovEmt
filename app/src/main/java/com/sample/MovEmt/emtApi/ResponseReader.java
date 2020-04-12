package com.sample.MovEmt.emtApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class ResponseReader {
    public String getResponse(HttpURLConnection connection){
        String inputLine;
        StringBuilder content = new StringBuilder();

        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
