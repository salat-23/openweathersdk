package com.salat23.openweather.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public final class WeatherResponse {

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("main")
    private Temperature temperature;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("dt")
    private long datetime;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("timezone")
    private int timezone;

    @SerializedName("name")
    private String name;

    @Getter
    @AllArgsConstructor
    public static class Weather {
        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

    }

    @Getter
    @AllArgsConstructor
    public static class Temperature {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

    }

    @Getter
    @AllArgsConstructor
    public static class Wind {
        @SerializedName("speed")
        private double speed;

    }

    @Getter
    @AllArgsConstructor
    public static class Sys {
        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

    }

}