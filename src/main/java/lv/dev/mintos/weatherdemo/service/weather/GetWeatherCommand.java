package lv.dev.mintos.weatherdemo.service.weather;

import an.awesome.pipelinr.Command;
import lv.dev.mintos.weatherdemo.model.Location;

public record GetWeatherCommand(Location location) implements Command<WeatherInfo> {

}
