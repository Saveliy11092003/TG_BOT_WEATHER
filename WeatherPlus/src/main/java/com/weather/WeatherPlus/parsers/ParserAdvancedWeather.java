package com.weather.WeatherPlus.parsers;

import com.weather.WeatherPlus.model.User;
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

public class ParserAdvancedWeather {
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
        double feels_like = jsonObject.getJSONObject("main").getDouble("feels_like");
        double temp_min = jsonObject.getJSONObject("main").getDouble("temp_min");
        double temp_max = jsonObject.getJSONObject("main").getDouble("temp_max");
        double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        double pressure = jsonObject.getJSONObject("main").getDouble("pressure");
        String precipitation = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

        double temp;
        double temp_fl;
        double t_min;
        double t_max;
        double press;
        double wind;


        if(storeUnits.getTemperature().equals(TEMPERATURE.C)) {
            temp = round(temperature - 273);
            temp_fl = round(feels_like - 273);
            t_min = round(temp_min - 273);
            t_max = round(temp_max - 273);
        } else {
            temp = temperature;
            temp_fl = feels_like;
            t_min = temp_min;
            t_max = temp_max;
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
                ",\n Feels like: " + temp_fl + " " + storeUnits.getTemperature() +
                ",\n Wind speed: " + wind + " " + storeUnits.getWindSpeed() +
                ",\n Pressure: " + press + " " + storeUnits.getPressure() +
                ",\n Humidity: " + humidity + "% " +
                ",\n Min temperature: " + t_min + " " + storeUnits.getTemperature() +
                ",\n Max temperature: " + t_max + " " + storeUnits.getTemperature() +
                ",\n Precipitation: " + precipitation;
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
