package com.weather.WeatherPlus.units;

public enum PRESSURE {
    MMHG("mmHG"), HPA("hPA");

    String unit;
    PRESSURE(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return unit;
    }
}
