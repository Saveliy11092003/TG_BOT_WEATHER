package com.weather.WeatherPlus.parsers;

import com.weather.WeatherPlus.model.UserRepository;
import com.weather.WeatherPlus.units.PRESSURE;
import com.weather.WeatherPlus.units.StoreUnits;
import com.weather.WeatherPlus.units.TEMPERATURE;
import com.weather.WeatherPlus.units.WIND_SPEED;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import static java.lang.Math.round;

import com.weather.WeatherPlus.model.User;


public class ParserBaseWeather {
    public String parse(StoreUnits storeUnits, UserRepository userRepository, Long chatId) throws IOException {

        String city = "Novosibirsk";

        Optional<User> user = userRepository.findById(chatId);
        if(user.isPresent()) {
            User foundUser = user.get();
            city = foundUser.getCity();
        }

        String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",ru&APPID=1ad07f1a062c4944c991c676c873d2c3");


        JSONObject jsonObject = new JSONObject(output);
        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        double pressure = jsonObject.getJSONObject("main").getDouble("pressure");


        double temp;
        double press;
        double wind;


        if(storeUnits.getTemperature().equals(TEMPERATURE.C)) {
            temp = round(temperature - 273);
        } else {
            temp = temperature;
        }

        if(storeUnits.getWindSpeed().equals(WIND_SPEED.KMH)) {
            wind = round(windSpeed * 3.6);
        } else {
            wind = windSpeed;
        }

        if(storeUnits.getPressure().equals(PRESSURE.MMHG)) {
            press = round(pressure * 0.7500616827);
        } else {
            press = pressure;
        }



        return  "WEATHER IN " + city +
                ",\n Temperature: " + temp + " " + storeUnits.getTemperature() +
                ",\n Wind speed: " + wind + " " + storeUnits.getWindSpeed() +
                ",\n Pressure: " + press + " " + storeUnits.getPressure();
    }

    public static String getUrlContent(String urlAdress) throws IOException {
        StringBuffer content = new StringBuffer();
        URL url = new URL(urlAdress);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
        String line;
        while((line = bufferedReader.readLine()) != null){
            content.append(line + "\n");
        }
        bufferedReader.close();
        return content.toString();
    }
}
