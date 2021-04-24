package lv.dev.mintos.weatherdemo.service.weather;

public record WeatherInfo(String description,
                          Double temperature,
                          Double feelsLike,
                          Double humidity,
                          Double windSpeed,
                          Integer windDirection) {
}
