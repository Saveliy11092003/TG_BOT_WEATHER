package com.weather.WeatherPlus.getters;

import com.vdurmont.emoji.EmojiParser;
import com.weather.WeatherPlus.model.UserRepository;
import com.weather.WeatherPlus.parsers.ParserRecommendationWeather;

import java.io.IOException;

public class GetterRecommendationWeather {
    UserRepository userRepository;
    Long chatId;

    public GetterRecommendationWeather(UserRepository userRepository, Long chatId) {
        this.userRepository = userRepository;
        this.chatId = chatId;
    }

    public String getRecommendation() throws IOException {
        ParserRecommendationWeather parser = new ParserRecommendationWeather();
        ParserRecommendationWeather.RecommendationInfoDto recommendationInfo = parser.parseRecommendationInfo(userRepository, chatId);
        double temperature = recommendationInfo.getTemperature() - 273.15;
        String precipitation = recommendationInfo.getPrecipitation().toLowerCase();
        String answer;

        if (temperature >= 30) {
            answer = EmojiParser.parseToUnicode("\uD83D\uDD25 ") +
                    "The sun is scorching outside, so you should stay at home if possible." +
                    "\n\nIf you need to go outside, you should give preference to light clothing made from natural materials. " +
                    "A headdress is a must!";
            if (precipitation.equals("rain")) {
                answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF27 ") +
                        "It's also raining outside. You need to take an umbrella or a thin raincoat with you.";
            }
            return answer;
        }

        if (temperature >= 20) {
            answer = EmojiParser.parseToUnicode(":sunny: ") +
                    "It’s warm outside, so you should wear comfortable clothes made from light natural materials. " +
                    "Don't forget to apply sunscreen and wear a hat!";
            if (precipitation.equals("rain")) {
                answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF27 ") +
                        "It's also raining outside. You need to take an umbrella or a thin raincoat with you.";
            }

            return answer;
        }

        if (temperature >= 10) {
            answer = EmojiParser.parseToUnicode(":relaxed: ") + "It's cool outside, " +
                    "so you should dress warmly: a sweater, jeans and light trousers. " +
                    "I recommend taking a warm jacket with you if there is a strong wind!";
            if (precipitation.equals("rain")) {
                answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF27 ") + "It's also raining outside, " +
                        "so it is worth taking an umbrella and wearing waterproof shoes.";
            }

            return answer;
        }

        if (temperature >= 0) {
            answer = EmojiParser.parseToUnicode("\uD83C\uDF24 ") + "To go outside today, you should wear a warm jacket, " +
                    "and don’t forget about a hat, scarf and gloves!";
            if (precipitation.equals("rain") || precipitation.equals("snow")) {
                answer += EmojiParser.parseToUnicode("\n\n:cloud: ") + "It's " + precipitation +
                        "ing outside now, so you need to take an umbrella with you and wear warmer shoes.";
            }

            return answer;
        }

        if (temperature >= -10) {
            answer = EmojiParser.parseToUnicode("\uD83C\uDF2C ") + "It's getting cold outside, " +
                    "so you should put on a warmer jacket or coat, take out a hat and gloves.";
            if (precipitation.equals("rain") || precipitation.equals("snow")) {
                answer += EmojiParser.parseToUnicode("\n\n:cloud: ") + "It's " + precipitation +
                        "ing now, so remember that in such conditions it is important " +
                        "to ensure that your clothes are not only warm, but also protected from moisture and wind!";
            }

            return answer;

        }

        if (temperature >= -20) {
            answer = EmojiParser.parseToUnicode(":snowman: ") + "It feels cold outside, so you need to dress warmly! " +
                    "Today you should wear a sweater and warm pants, and a down jacket on top.";
            if (precipitation.equals("snow")) {
                answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF28 ") + "It's also snowing outside, " +
                        "so you need to wear a scarf and gloves to protect yourself.";
            }

            return answer;
        }

        if (temperature >= -30) {
            answer = EmojiParser.parseToUnicode("\uD83E\uDDCA ") + "The temperature is quite low now, so you need to wear a warm insulated coat, " +
                    "warm trousers and shoes. It is important not to forget about gloves, a scarf and a hat!";
            if (precipitation.equals("snow")) {
                answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF28 ") + "It's also snowing, " +
                        "so it's important to wear a waterproof jacket and insulated shoes!";
            }

            return answer;
        }

        answer = EmojiParser.parseToUnicode("\uD83E\uDD76 ") + "Temperatures outside are very low at the moment, " +
                "so you should exercise caution and stay home if possible! " +
                "If you still need to go outside, then you should wear thermal underwear, woolen socks, " +
                "a hat, gloves, warm pants and a down jacket.";
        if (precipitation.equals("snow")) {
            answer += EmojiParser.parseToUnicode("\n\n\uD83C\uDF28 ") + "It's snowing outside, so wear waterproof shoes!";
        }

        return answer;
    }
}
