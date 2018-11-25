package tikal.spotify.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationWeather {
    private String weatherType;
    private String country;
    private String city;
}
