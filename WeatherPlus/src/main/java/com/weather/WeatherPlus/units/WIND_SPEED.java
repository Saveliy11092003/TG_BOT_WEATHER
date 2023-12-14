package com.weather.WeatherPlus.units;

public enum WIND_SPEED {
    MS("m/s"), KMH("km/h");

    String unit;

    WIND_SPEED(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return unit;
    }
}
