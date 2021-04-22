package lv.dev.mintos.weatherdemo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class IpLocation {

    @Id
    @NotBlank
    private String ip;

    @ManyToOne
    @JoinColumn(name="location_id", referencedColumnName = "id")
    private Location location;

    public IpLocation() {
    }

    public IpLocation(String ip, Location location) {
        this.ip = ip;
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
