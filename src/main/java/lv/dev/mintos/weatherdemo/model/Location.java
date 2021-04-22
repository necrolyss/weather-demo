package lv.dev.mintos.weatherdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "location", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "country", "city"
        })
})
@XmlRootElement
public class Location implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String city;

    @NotBlank
    @JsonProperty("country_name")
    private String country;

    public Location() {
    }

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id)
                && Objects.equals(city, location.city)
                && Objects.equals(country, location.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, country);
    }

    @Override
    public String toString() {
        return "{id=" + id +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
