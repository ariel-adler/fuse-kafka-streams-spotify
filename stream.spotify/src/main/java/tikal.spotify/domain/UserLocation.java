package tikal.spotify.domain;

import lombok.Data;

@Data
public class UserLocation {
    private String email;
    private String country;
    private String city;
}
