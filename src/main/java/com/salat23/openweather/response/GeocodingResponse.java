package com.salat23.openweather.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class GeocodingResponse {
    private final String name;
    private final double lat;
    private final double lon;
}
