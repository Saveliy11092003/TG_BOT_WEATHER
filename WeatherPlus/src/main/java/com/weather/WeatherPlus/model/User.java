package com.weather.WeatherPlus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.glassfish.grizzly.http.util.TimeStamp;

@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatId;

    private String city;
    private String userName;


    public Long getChatId() {
        return chatId;
    }

    public String getCity() {
        return city;
    }

    public String getUserName() {
        return userName;
    }



    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", city='" + city + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
