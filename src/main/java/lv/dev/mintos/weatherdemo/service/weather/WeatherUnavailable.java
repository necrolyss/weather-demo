package lv.dev.mintos.weatherdemo.service.weather;

import lv.dev.mintos.weatherdemo.infra.RestException;
import lv.dev.mintos.weatherdemo.model.Location;

public class WeatherUnavailable extends RestException {

    public WeatherUnavailable(Location location) {
        super("exception.weather-unavailable", location);
    }
}
