package com.weather.WeatherPlus.getters;

import com.weather.WeatherPlus.model.UserRepository;
import com.weather.WeatherPlus.parsers.ParserAdvancedWeather;
import com.weather.WeatherPlus.parsers.ParserBaseWeather;
import com.weather.WeatherPlus.units.StoreUnits;

import java.io.IOException;

public class GetterAdvancedWeather {

    StoreUnits storeUnits;
    UserRepository userRepository;

    Long chatId;

    public GetterAdvancedWeather(StoreUnits storeUnits, UserRepository userRepository, Long chatId) {
        this.storeUnits = storeUnits;
        this.userRepository = userRepository;
        this.chatId = chatId;
    }

    public String getWeather() throws IOException {
        ParserAdvancedWeather parser = new ParserAdvancedWeather();
        String message = parser.parse(storeUnits, userRepository, chatId);
        return message;
    }
}
