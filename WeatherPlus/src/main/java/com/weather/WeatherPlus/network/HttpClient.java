package com.weather.WeatherPlus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpClient {
    public static String getUrlContent(String urlAddress) throws IOException {
        StringBuffer content = new StringBuffer();
        URL url = new URL(urlAddress);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
        String line;
        while((line = bufferedReader.readLine()) != null){
            content.append(line).append("\n");
        }
        bufferedReader.close();
        return content.toString();
    }
}
