package com.salat23.openweather.provider;

import com.google.gson.Gson;
import com.salat23.openweather.OpenWeatherException;
import com.salat23.openweather.request.OpenWeatherWeatherRequest;
import com.salat23.openweather.response.WeatherResponse;
import lombok.Builder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Builder
public class DefaultWeatherProvider implements OpenWeatherProvider<OpenWeatherWeatherRequest, WeatherResponse> {
    private static final Gson GSON = new Gson();

    @Builder.Default
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public WeatherResponse get(OpenWeatherWeatherRequest request) throws OpenWeatherException {
        HttpRequest weatherRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format(
                        "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s",
                        request.getGeocodingResponse().getLat(),
                        request.getGeocodingResponse().getLon(),
                        request.getApiKey()
                )))
                .build();
        try {
            HttpResponse<String> weatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            if (weatherResponse.statusCode() == 401) throw new OpenWeatherException("Invalid API key");
            String body = weatherResponse.body();
            return GSON.fromJson(body, WeatherResponse.class);
        } catch (IOException | InterruptedException exception) {
            throw new OpenWeatherException("Could not obtain weather data", exception);
        }
    }
}
