package com.salat23.openweather.options;

import com.salat23.openweather.provider.DefaultGeocodingProvider;
import com.salat23.openweather.provider.DefaultWeatherProvider;
import com.salat23.openweather.provider.OpenWeatherProvider;
import com.salat23.openweather.request.OpenWeatherGeocodingRequest;
import com.salat23.openweather.request.OpenWeatherWeatherRequest;
import com.salat23.openweather.response.GeocodingResponse;
import com.salat23.openweather.response.WeatherResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenWeatherOptions {
    /**
     * Mode at which instance will operate
     */
    @Builder.Default
    private final OpenWeatherMode mode = OpenWeatherMode.ON_DEMAND;
    /**
     * Provider used when fetching geocoding
     */
    @Builder.Default
    private final OpenWeatherProvider<OpenWeatherGeocodingRequest, GeocodingResponse> geocodingProvider = DefaultGeocodingProvider.builder().build();
    /**
     * Provider used when fetching weather
     */
    @Builder.Default
    private final OpenWeatherProvider<OpenWeatherWeatherRequest, WeatherResponse> weatherProvider = DefaultWeatherProvider.builder().build();
}
