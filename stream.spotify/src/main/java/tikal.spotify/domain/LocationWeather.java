package tikal.spotify.domain;

import lombok.Data;

@Data
public class LocationWeather {
    private String weatherType;
    private String country;
    private String city;
}
