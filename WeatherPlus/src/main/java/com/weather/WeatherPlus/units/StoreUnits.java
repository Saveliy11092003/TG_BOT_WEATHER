package com.weather.WeatherPlus.units;

import org.springframework.boot.autoconfigure.ssl.JksSslBundleProperties;

public class StoreUnits {
    TEMPERATURE temperature;
    PRESSURE pressure;
    WIND_SPEED windSpeed;

    public StoreUnits(TEMPERATURE temperature, PRESSURE pressure, WIND_SPEED windSpeed) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
    }

    public TEMPERATURE getTemperature() {
        return temperature;
    }

    public PRESSURE getPressure() {
        return pressure;
    }

    public WIND_SPEED getWindSpeed() {
        return windSpeed;
    }

    public void setTemperature(TEMPERATURE temperature) {
        this.temperature = temperature;
    }

    public void setPressure(PRESSURE pressure) {
        this.pressure = pressure;
    }

    public void setWindSpeed(WIND_SPEED windSpeed) {
        this.windSpeed = windSpeed;
    }
}
