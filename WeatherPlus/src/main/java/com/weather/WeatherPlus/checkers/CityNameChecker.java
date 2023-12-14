package com.weather.WeatherPlus.checkers;

import com.weather.WeatherPlus.network.HttpClient;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CityNameChecker {
    public boolean isCity(String cityName) throws IOException {

        String outputRU = HttpClient.getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + ",ru&APPID=1ad07f1a062c4944c991c676c873d2c3");
        String outputUK = HttpClient.getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + ",uk&APPID=1ad07f1a062c4944c991c676c873d2c3");


        JSONObject jsonObjectRU = new JSONObject(outputRU);
        String cityRU = jsonObjectRU.getString("name");
        log.info("city Ru " + cityRU);
        System.out.println("city Ru " + cityRU);

        JSONObject jsonObjectUK = new JSONObject(outputUK);
        String cityUK = jsonObjectUK.getString("name");
        log.info("city UK " + cityUK);
        System.out.println("city UK " + cityUK);


        return cityRU.equals(cityName) || cityUK.equals(cityName);
    }

}
