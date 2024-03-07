package com.salat23.openweather.request;

import lombok.Data;

@Data
public class OpenWeatherGeocodingRequest {
    private String cityName;
    private String apiKey;

    public OpenWeatherGeocodingRequest(String cityName, String apiKey) {
        this.cityName = cityName;
        this.apiKey = apiKey;
    }
}
