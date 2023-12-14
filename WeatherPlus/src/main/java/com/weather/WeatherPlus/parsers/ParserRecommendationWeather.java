package com.weather.WeatherPlus.parsers;


import com.weather.WeatherPlus.model.User;
import com.weather.WeatherPlus.model.UserRepository;
import com.weather.WeatherPlus.network.HttpClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class ParserRecommendationWeather {
    public RecommendationInfoDto parseRecommendationInfo(UserRepository userRepository, Long chatId) throws IOException {

        String city = "Novosibirsk";

        Optional<User> user = userRepository.findById(chatId);
        if(user.isPresent()) {
            User foundUser = user.get();
            city = foundUser.getCity();
        }

        String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",ru&APPID=1ad07f1a062c4944c991c676c873d2c3");


        JSONObject jsonObject = new JSONObject(output);
        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        String precipitation = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

        RecommendationInfoDto dto = new RecommendationInfoDto(temperature, precipitation);
        return dto;
    }

    @Getter
    @AllArgsConstructor
    public static class RecommendationInfoDto {
        private double temperature;
        private String precipitation;
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

