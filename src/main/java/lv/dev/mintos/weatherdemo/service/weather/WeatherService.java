package lv.dev.mintos.weatherdemo.service.weather;

import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import lv.dev.mintos.weatherdemo.infra.Resilient;
import lv.dev.mintos.weatherdemo.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class WeatherService {

    public static final String CACHE_NAME = "location_weather";

    private final GetWeatherHandler getWeatherHandler;
    private final Resilient resilient;

    @Autowired
    public WeatherService(GetWeatherHandler getWeatherHandler, Resilient resilient) {
        this.getWeatherHandler = getWeatherHandler;
        this.resilient = resilient;
    }

    @Cacheable(CACHE_NAME)
    public WeatherInfo currentWeather(Location location) {
        Pipeline pipeline = new Pipelinr()
                .with(() -> Stream.of(getWeatherHandler))
                .with(() -> Stream.of(resilient));
        return pipeline.send(new GetWeatherCommand(location));
    }

}
