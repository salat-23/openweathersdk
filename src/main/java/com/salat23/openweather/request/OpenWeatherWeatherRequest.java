package com.salat23.openweather.request;

import com.salat23.openweather.response.GeocodingResponse;
import lombok.Data;

@Data
public class OpenWeatherWeatherRequest {
    private GeocodingResponse geocodingResponse;
    private String apiKey;

    public OpenWeatherWeatherRequest(GeocodingResponse geocodingResponse, String apiKey) {
        this.geocodingResponse = geocodingResponse;
        this.apiKey = apiKey;
    }
}
