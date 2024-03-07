package com.salat23.openweather;

public class OpenWeatherException extends Exception {

    public OpenWeatherException() {
    }

    public OpenWeatherException(String message) {
        super(message);
    }

    public OpenWeatherException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenWeatherException(Throwable cause) {
        super(cause);
    }

    public OpenWeatherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
