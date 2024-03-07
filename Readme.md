![OpenWeather SDK](img/banner.png)

# OpenWeather SDK

The OpenWeather SDK is a simple Java library for interacting with the OpenWeather API, providing easy access for fetching weather by city name.

## Features

- Simple and intuitive API calls to retrieve weather data
- Caching mechanism to optimize API usage and improve performance
- Flexible configuration options through `OpenWeatherOptions`
- Automatic polling for continuous weather updates

## Getting Started

To use the OpenWeather SDK, you'll need an API key from OpenWeather. If you don't have one, you can sign up
at [OpenWeatherMap](https://home.openweathermap.org/users/sign_up) and obtain your API key from your dashboard.

### Installation

Add Jitpack to repositories in your gradle.build and add the following dependency:
```groovy
repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.salat-23:openweathersdk:1.0.0'
}
```

### Quick Start Guide

Here's a quick example to get the current weather for a city:

```java
import com.salat23.openweather.OpenWeather;
import com.salat23.openweather.options.OpenWeatherOptions;
import com.salat23.openweather.response.WeatherResponse;

public class WeatherExample {
    public static void main(String[] args) {
        try {
            // You can modify options if you need to implement mocks for tests or use external services for geocoding or weather
            OpenWeatherOptions options = OpenWeatherOptions.builder().build();
            try (OpenWeather openWeather = OpenWeather.create("YOUR_API_KEY", options)) {
                WeatherResponse weather = openWeather.getWeather("Noginsk");
                System.out.println("Temperature in Noginsk: " + weather.getTemperature().getTemp());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```