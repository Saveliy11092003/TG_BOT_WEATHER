package com.weather.WeatherPlus.service;

import com.weather.WeatherPlus.checkers.CityNameChecker;
import com.weather.WeatherPlus.config.BotConfig;
import com.weather.WeatherPlus.getters.GetterAdvancedWeather;
import com.weather.WeatherPlus.getters.GetterBaseWeather;
import com.weather.WeatherPlus.getters.GetterRecommendationWeather;
import com.weather.WeatherPlus.model.User;
import com.weather.WeatherPlus.model.UserRepository;
import com.weather.WeatherPlus.units.PRESSURE;
import com.weather.WeatherPlus.units.StoreUnits;
import com.weather.WeatherPlus.units.TEMPERATURE;
import com.weather.WeatherPlus.units.WIND_SPEED;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    private boolean waitCity;
    final BotConfig config;

    StoreUnits storeUnits;

    public TelegramBot(BotConfig config) {

        this.config = config;

        storeUnits = new StoreUnits(TEMPERATURE.C, PRESSURE.HPA, WIND_SPEED.MS);

        List<BotCommand> listOfCommands = new ArrayList<>();

        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/weather_base", "get a base weather"));
        listOfCommands.add(new BotCommand("/weather_advanced", "get a advanced weather"));
        listOfCommands.add(new BotCommand("/recommendation_of_clothes", "recomendation of clothers"));
        listOfCommands.add(new BotCommand("/change_utils", "change utils"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update on");
        if(update.hasMessage() && update.getMessage().hasText() && !waitCity) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start":{

                    registerUser(update.getMessage());
                    startCommandReceive(chatId, update.getMessage().getChat().getFirstName());

                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("Enter the city: ");
                    try {
                        execute(message);
                    }catch (TelegramApiException e) {
                        log.error("Error occurred: " + e.getMessage());
                    }
                    waitCity = true;
                    break;

                }

                case "Help":
                case "/help": {
                    sendMessage(chatId, HELP_TEXT);
                    break;
                }

                case "Base weather":
                case "/weather_base": {
                    try {
                        weatherBaseCommandReceive(chatId, storeUnits, userRepository);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "/weather_advanced": {
                    try {
                        weatherAdvancedCommandReceive(chatId, storeUnits, userRepository);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "Change city":
                case "/change_city": {
                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("Enter the city: ");
                    try {
                        execute(message);
                    }catch (TelegramApiException e) {
                        log.error("Error occurred: " + e.getMessage());
                    }
                    waitCity = true;
                    break;
                }

                case "Change units":
                case "/change_utils": {
                    try {
                        changeTempCommandReceive(chatId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "Cloth recommendations":
                case "/recommendation_of_clothes": {
                    try {
                        recommendationCommandReceive(userRepository, chatId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                default: {
                    sendMessage(chatId, "Sorry, command was not recognized");
                }

            }
        } else if (update.hasMessage() && update.getMessage().hasText() && waitCity) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText("You enter: " + update.getMessage().getText());
            try {
                execute(message);
            }catch (TelegramApiException e) {
                log.error("Error occurred: " + e.getMessage());
            }

            var chatId = update.getMessage().getChatId();
            var chat = update.getMessage().getChat();

            String city = update.getMessage().getText();
            CityNameChecker cityNameChecker = new CityNameChecker();
            try {
                if (!cityNameChecker.isCity(city)) {


                } else {
                    User user = new User();
                    user.setChatId(chatId);
                    user.setUserName(chat.getUserName());
                    user.setCity(city);
                    userRepository.save(user);
                }
            } catch (IOException e) {
                waitCity = false;
                String answer = "There is no such city";
                SendMessage msg = new SendMessage();
                msg.setChatId(String.valueOf(update.getMessage().getChatId()));
                msg.setText(answer);
                try {
                    execute(msg);
                }catch (TelegramApiException ex) {
                    log.error("Error occurred: " + ex.getMessage());
                }
                throw new RuntimeException(e);
            }
            waitCity = false;
        }
        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if(callBackData.equals("BUTTON_C")) {
                String text = "You pressed C";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

                storeUnits.setTemperature(TEMPERATURE.C);

                try {
                    changeWindCommandReceive(chatId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if(callBackData.equals("BUTTON_K")) {
                String text = "You pressed K";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

                storeUnits.setTemperature(TEMPERATURE.K);

                try {
                    changeWindCommandReceive(chatId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (callBackData.equals("KM_H_BUTTON")) {
                String text = "You pressed km/h";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

                storeUnits.setWindSpeed(WIND_SPEED.KMH);

                try {
                    changePressureCommandReceive(chatId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (callBackData.equals("MS_BUTTON")) {
                String text = "You pressed m/s";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

                storeUnits.setWindSpeed(WIND_SPEED.MS);

                try {
                    changePressureCommandReceive(chatId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (callBackData.equals("MM_HG_BUTTON")) {
                String text = "You pressed mmHG";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                storeUnits.setPressure(PRESSURE.MMHG);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }
            }else if (callBackData.equals("H_PA_BUTTON")) {
                String text = "You pressed hPa";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                storeUnits.setPressure(PRESSURE.HPA);

                try {
                    execute(message);
                }catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }
            }
        }
    }


    private static final String HELP_TEXT = "This bot is designed to provide weather conditions in your city." +
            "\nYou can use one of the following commands by typing it or using the main menu:" +
            "\n\nType /start to see the welcome message" +
            "\n\nType /weather_base to get basic information about the current weather in your city" +
            "\n\nType /weather_advanced to get more detailed information about the current weather in your city" +
            "\n\nType /recommendation_of_clothes to see clothing recommendations depending on the weather in your city at the moment" +
            "\n\nType /change_utils to change the units of temperature, pressure and wind speed" +
            "\n\nType /help to see this message again";

    private void recommendationCommandReceive(UserRepository userRepository, Long chatId) throws IOException {
        GetterRecommendationWeather getterRecommendation = new GetterRecommendationWeather(userRepository, chatId);
        String answer = getterRecommendation.getRecommendation();
        sendMessage(chatId, answer);
    }

    private void registerUser(Message msg) {
        log.info("begin registerUser");
        if(userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setUserName(chat.getUserName());

            userRepository.save(user);
            log.info("save user: " + user);
        }
        log.info("end registerUser");
    }

    private void startCommandReceive(long chatId, String firstName) {
        String answer = "Hi, " + firstName + ",  nice to meet you!";
        //log.info("Replied to user");
        sendMessage(chatId, answer);
    }


    private void weatherBaseCommandReceive(long chatId, StoreUnits storeUnits, UserRepository userRepository) throws IOException {
        GetterBaseWeather getterWeather = new GetterBaseWeather(storeUnits, userRepository, chatId);
        String answer = getterWeather.getWeather();
        sendMessage(chatId, answer);
    }

    private void weatherAdvancedCommandReceive(long chatId, StoreUnits storeUnits, UserRepository userRepository) throws IOException {
        log.info("weather advanced received");
        GetterAdvancedWeather getterWeather = new GetterAdvancedWeather(storeUnits, userRepository, chatId);
        String answer = getterWeather.getWeather();
        sendMessage(chatId, answer);
    }
    private void changeCityCommandReceive(long chatId) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String city = scanner.nextLine();
        sendMessage(chatId, city);
        scanner.close();
    }

    private void changeTempCommandReceive(long chatId) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Select util of temperature:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("C");
        button1.setCallbackData("BUTTON_C");

        var button2 = new InlineKeyboardButton();
        button2.setText("K");
        button2.setCallbackData("BUTTON_K");

        row1.add(button1);
        row1.add(button2);

        rows.add(row1);

        markup.setKeyboard(rows);

        message.setReplyMarkup(markup);

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private void changeWindCommandReceive(long chatId) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Select speed wind");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("m/s");
        button1.setCallbackData("MS_BUTTON");

        var button2 = new InlineKeyboardButton();
        button2.setText("km/h");
        button2.setCallbackData("KM_H_BUTTON");

        row1.add(button1);
        row1.add(button2);

        rows.add(row1);

        markup.setKeyboard(rows);

        message.setReplyMarkup(markup);

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }


    private void changePressureCommandReceive(long chatId) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Select speed wind");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("mmHg");
        button1.setCallbackData("MM_HG_BUTTON");

        var button2 = new InlineKeyboardButton();
        button2.setText("hPa");
        button2.setCallbackData("H_PA_BUTTON");

        row1.add(button1);
        row1.add(button2);

        rows.add(row1);

        markup.setKeyboard(rows);

        message.setReplyMarkup(markup);

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }




    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add("Help");
        row1.add("Base weather");
        row1.add("/weather_advanced");
        row2.add("Cloth recommendations");
        row2.add("Change units");
        row2.add("Change city");


        keyboardRows.add(row1);
        keyboardRows.add(row2);


        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

}

