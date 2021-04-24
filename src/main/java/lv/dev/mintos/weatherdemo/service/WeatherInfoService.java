package lv.dev.mintos.weatherdemo.service;

import lv.dev.mintos.weatherdemo.model.Location;
import lv.dev.mintos.weatherdemo.model.LocationConditions;
import lv.dev.mintos.weatherdemo.repository.LocationConditionsRepository;
import lv.dev.mintos.weatherdemo.service.location.LocationService;
import lv.dev.mintos.weatherdemo.service.weather.WeatherInfo;
import lv.dev.mintos.weatherdemo.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherInfoService {

    private final LocationService locationService;
    private final WeatherService weatherService;
    private final LocationConditionsRepository locationConditionsRepository;

    @Autowired
    public WeatherInfoService(LocationService locationService, WeatherService weatherService,
                              LocationConditionsRepository locationConditionsRepository) {
        this.locationService = locationService;
        this.weatherService = weatherService;
        this.locationConditionsRepository = locationConditionsRepository;
    }

    public WeatherInfo currentWeatherInfo(String userIp) {
        Location location = locationService.locationOf(userIp);
        WeatherInfo weatherInfo = weatherService.currentWeather(location);
        storeHistoricalData(location, weatherInfo);
        return weatherInfo;
    }

    private void storeHistoricalData(Location location, WeatherInfo weatherInfo) {
        LocationConditions locationConditions = locationConditions(location, weatherInfo);
        locationConditionsRepository.save(locationConditions);
    }

    private LocationConditions locationConditions(Location location, WeatherInfo weatherInfo) {
        LocationConditions locationConditions = new LocationConditions();
        locationConditions.setLocation(location);
        locationConditions.setDescription(weatherInfo.description());
        locationConditions.setTemperature(weatherInfo.temperature());
        locationConditions.setFeelsLike(weatherInfo.feelsLike());
        locationConditions.setHumidity(weatherInfo.humidity());
        locationConditions.setWindSpeed(weatherInfo.windSpeed());
        locationConditions.setWindDirection(weatherInfo.windDirection());
        return locationConditions;
    }

}
