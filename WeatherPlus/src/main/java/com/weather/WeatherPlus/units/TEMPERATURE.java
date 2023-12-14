package com.weather.WeatherPlus.units;

public enum TEMPERATURE {
    K("K"), C("C");

    String unit;

    TEMPERATURE(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return unit;
    }
}
