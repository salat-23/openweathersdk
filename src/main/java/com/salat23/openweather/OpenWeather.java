package com.salat23.openweather;

import com.salat23.openweather.options.OpenWeatherMode;
import com.salat23.openweather.options.OpenWeatherOptions;
import com.salat23.openweather.provider.OpenWeatherProvider;
import com.salat23.openweather.request.OpenWeatherGeocodingRequest;
import com.salat23.openweather.request.OpenWeatherWeatherRequest;
import com.salat23.openweather.response.GeocodingResponse;
import com.salat23.openweather.response.WeatherResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class OpenWeather implements AutoCloseable {
    public static final int CACHE_LIVE_TIME_MINUTES = 10;
    public static final int CACHE_MAX_ELEMENTS = 10;
    public static final int POLLING_DELAY_MINUTES = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenWeather.class);
    private static final Set<String> USED_API_KEYS = new HashSet<>();
    // geocoding cache is shared for the sake of saving useless repeating requests
    private static final Map<String, GeocodingResponse> GEOCODING_CACHE = Collections.synchronizedMap(new HashMap<>());

    private final String apiKey;
    private final OpenWeatherProvider<OpenWeatherGeocodingRequest, GeocodingResponse> geocodingProvider;
    private final OpenWeatherProvider<OpenWeatherWeatherRequest, WeatherResponse> weatherProvider;

    private final FixedSizeQueue<String> latestCityNames = new FixedSizeQueue<>(CACHE_MAX_ELEMENTS);
    private final FixedSizeQueue<CachedWeatherResponse> latestWeatherResponsesCache = new FixedSizeQueue<>(CACHE_MAX_ELEMENTS);
    private final Map<String, CachedWeatherResponse> weatherResponseCache = Collections.synchronizedMap(new HashMap<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Creates instance with specified options
     * @param apiKey OpenWeather's api key. You can get one at <a href="https://home.openweathermap.org/api_keys"> OpenWeather's dashboard</a>
     * @param options specified options for this instance
     * @return OpenWeather instance
     * @throws OpenWeatherException
     */
    public static OpenWeather create(String apiKey, OpenWeatherOptions options) throws OpenWeatherException {
        if (USED_API_KEYS.contains(apiKey))
            throw new OpenWeatherException(String.format("Instance with api key %s already exists", apiKey));
        final OpenWeather instance = new OpenWeather(apiKey, options);
        USED_API_KEYS.add(apiKey);
        return instance;
    }

    /**
     * Creates instance with default options
     * @param apiKey OpenWeather's api key. You can get one at <a href="https://home.openweathermap.org/api_keys"> OpenWeather's dashboard</a>
     * @return OpenWeather instance
     * @throws OpenWeatherException
     */
    public static OpenWeather create(String apiKey) throws OpenWeatherException {
        return create(apiKey, OpenWeatherOptions.builder().build());
    }

    private OpenWeather(String apiKey, OpenWeatherOptions options) {
        this.apiKey = apiKey;
        this.geocodingProvider = options.getGeocodingProvider();
        this.weatherProvider = options.getWeatherProvider();

        if (options.getMode() == OpenWeatherMode.POLLING) {
            final Runnable poller = () -> {
                List<WeatherResponse> weatherResponses = new ArrayList<>();
                for (String cityName : latestCityNames) {
                    try {
                        weatherResponses.add(getWeather(cityName));
                    } catch (OpenWeatherException exception) {
                        LOGGER.error(String.format("Exception while polling new weather result for %s", cityName), exception);
                    }
                }
                for (WeatherResponse response : weatherResponses) {
                    latestWeatherResponsesCache.add(new CachedWeatherResponse(response));
                }
            };
            // start the task with an initial delay of 0 and repeat it every 10 minutes
            scheduler.scheduleAtFixedRate(poller, 0, POLLING_DELAY_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * Get weather for specified city
     * @param cityName the name of the city
     * @return WeatherResponse
     * @throws OpenWeatherException
     */
    public WeatherResponse getWeather(String cityName) throws OpenWeatherException {
        // try to validate and return a cached result
        for (CachedWeatherResponse cachedWeatherResponse : latestWeatherResponsesCache) {
            if (cachedWeatherResponse.getFetchedAt().plusMinutes(CACHE_LIVE_TIME_MINUTES).isAfter(LocalDateTime.now())) {
                latestWeatherResponsesCache.add(cachedWeatherResponse); // update insertion position
                return cachedWeatherResponse.getWeatherResponse();
            }
        }
        GeocodingResponse geocoding = getGeocodingFor(cityName);
        WeatherResponse weather = weatherProvider.get(new OpenWeatherWeatherRequest(geocoding, apiKey));
        weatherResponseCache.put(cityName, new OpenWeather.CachedWeatherResponse(weather));
        return weather;
    }

    private GeocodingResponse getGeocodingFor(String cityName) throws OpenWeatherException {
        GeocodingResponse cachedGeocoding = GEOCODING_CACHE.get(cityName);
        if (cachedGeocoding != null) return cachedGeocoding;
        GeocodingResponse geocoding = geocodingProvider.get(new OpenWeatherGeocodingRequest(cityName, apiKey));
        GEOCODING_CACHE.put(cityName, geocoding);
        return geocoding;
    }

    @Override
    public void close() {
        scheduler.shutdown();
        USED_API_KEYS.remove(apiKey);
    }

    @Getter
    private static class CachedWeatherResponse {
        private final WeatherResponse weatherResponse;
        private final LocalDateTime fetchedAt;

        public CachedWeatherResponse(WeatherResponse weatherResponse) {
            this.weatherResponse = weatherResponse;
            this.fetchedAt = LocalDateTime.now();
        }

    }
}
