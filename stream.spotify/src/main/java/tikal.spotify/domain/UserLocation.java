package tikal.spotify.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLocation {
    private String email;
    private String country;
    private String city;
}
