package com.salat23.openweather.options;

public enum OpenWeatherMode {
    /**
     * Make requests when getWeather is called
     */
    ON_DEMAND,
    /**
     * Make requests in the background once in 10 minutes
     */
    POLLING
}
