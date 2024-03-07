package com.salat23.openweather.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salat23.openweather.OpenWeatherException;
import com.salat23.openweather.request.OpenWeatherGeocodingRequest;
import com.salat23.openweather.response.GeocodingResponse;
import lombok.Builder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Builder
public class DefaultGeocodingProvider implements OpenWeatherProvider<OpenWeatherGeocodingRequest, GeocodingResponse> {
    private static final Gson GSON = new Gson();

    @Builder.Default
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public GeocodingResponse get(OpenWeatherGeocodingRequest request) throws OpenWeatherException {
        HttpRequest geoRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", request.getCityName(), request.getApiKey())))
                .build();
        try {
            HttpResponse<String> geocodingResponse = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            if (geocodingResponse.statusCode() == 401) throw new OpenWeatherException("Invalid API key");
            String body = geocodingResponse.body();
            Type listType = new TypeToken<List<GeocodingResponse>>() {
            }.getType();
            List<GeocodingResponse> geocodings = GSON.fromJson(body, listType);
            if (geocodings.isEmpty()) throw new OpenWeatherException(String.format("Could not find city %s", request.getCityName()));
            return geocodings.get(0);
        } catch (IOException | InterruptedException exception) {
            throw new OpenWeatherException("Could not obtain geocoding data", exception);
        }
    }
}
