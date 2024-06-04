package mozaique.centre.plonger.models;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Place {
    private String id;
    private String title;
    private String address;
    private String photoUrl;
    private String website;
    private String timeZone;
    private List<WorkingHour> workingHours;
    private int reviews;
    private String phoneNumber;
    private double rating;
    private String type;
    private List<String> serviceOptions;
    private double latitude;
    private double longitude;

}
