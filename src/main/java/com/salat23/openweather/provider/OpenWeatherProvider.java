package com.salat23.openweather.provider;

import com.salat23.openweather.OpenWeatherException;

/**
 * Interface used to implement custom providers to use with external services or for testing
 * @param <INPUT> input parameter type
 * @param <RESULT> resulting parameter type
 */
public interface OpenWeatherProvider<INPUT, RESULT> {
    RESULT get(INPUT input) throws OpenWeatherException;
}
