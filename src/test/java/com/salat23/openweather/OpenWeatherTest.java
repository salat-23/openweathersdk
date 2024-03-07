package com.salat23.openweather;

import com.salat23.openweather.options.OpenWeatherOptions;
import com.salat23.openweather.provider.DefaultGeocodingProvider;
import com.salat23.openweather.provider.DefaultWeatherProvider;
import com.salat23.openweather.response.GeocodingResponse;
import com.salat23.openweather.response.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenWeatherTest {
    private DefaultWeatherProvider mockWeatherProvider;
    private DefaultGeocodingProvider mockGeocodingProvider;

    private GeocodingResponse getMockGeocodingResponse() {
        return new GeocodingResponse("Noginsk", 23.23, 23.23);
    }

    private WeatherResponse getMockWeatherResponse() {
        return new WeatherResponse(
                Collections.singletonList(new WeatherResponse.Weather("Clouds", "scattered clouds")), // Assuming you have just one Weather object for simplicity
                new WeatherResponse.Temperature(289.95, 287.85),
                10000,
                new WeatherResponse.Wind(1.5),
                1600425600L,
                new WeatherResponse.Sys(1600410600L, 1600456500L),
                3600,
                "Noginsk"
        );
    }

    @BeforeEach
    void setUp() throws OpenWeatherException {
        mockWeatherProvider = mock(DefaultWeatherProvider.class);
        mockGeocodingProvider = mock(DefaultGeocodingProvider.class);

        when(mockGeocodingProvider.get(any()))
                .thenReturn(getMockGeocodingResponse());

        when(mockWeatherProvider.get(any()))
                .thenReturn(getMockWeatherResponse());
    }

    @Test
    void when_creatingInstancesWithDuplicateApiKeys_throwsOpenWeatherException() {
        OpenWeatherOptions options = OpenWeatherOptions.builder()
                .geocodingProvider(mockGeocodingProvider)
                .weatherProvider(mockWeatherProvider)
                .build();

        assertDoesNotThrow(() -> {
            var firstInstance = OpenWeather.create("testKey1", options);
            assertThrows(OpenWeatherException.class, () -> {
                var secondInstanceWithDuplicateApiKey = OpenWeather.create("testKey1", options);
                secondInstanceWithDuplicateApiKey.close();
            });
            firstInstance.close();
        });
    }

    @Test
    void when_creatingInstanceWithApiKeyThatWasUsedInClosedInstance_shouldNotThrow() {
        OpenWeatherOptions options = OpenWeatherOptions.builder()
                .geocodingProvider(mockGeocodingProvider)
                .weatherProvider(mockWeatherProvider)
                .build();

        assertDoesNotThrow(() -> {
            var firstInstance = OpenWeather.create("testKey1", options);
            firstInstance.close();
            var forthInstance = OpenWeather.create("testKey1", options);
            forthInstance.close();
        });
    }

    @Test
    void when_gettingWeatherForCity_getResultWithoutExceptions() {
        OpenWeatherOptions options = OpenWeatherOptions.builder()
                .geocodingProvider(mockGeocodingProvider)
                .weatherProvider(mockWeatherProvider)
                .build();

        assertDoesNotThrow(() -> {
            try (var instance = OpenWeather.create("testKey1", options)) {
                var weather = instance.getWeather("Noginsk");
                assertEquals(289.95, weather.getTemperature().getTemp());
            }
        });
    }
}