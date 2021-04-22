package lv.dev.mintos.weatherdemo.rest;

import lv.dev.mintos.weatherdemo.infra.ApiResponse;
import lv.dev.mintos.weatherdemo.service.IpService;
import lv.dev.mintos.weatherdemo.service.WeatherInfoService;
import lv.dev.mintos.weatherdemo.service.weather.WeatherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/weather")
class WeatherController {

    private final IpService ipService;
    private final WeatherInfoService weatherInfoService;

    @Autowired
    WeatherController(IpService ipService, WeatherInfoService weatherInfoService) {
        this.ipService = ipService;
        this.weatherInfoService = weatherInfoService;
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse> currentWeather(HttpServletRequest request) {
        return ResponseEntity.ok(new ApiResponse(weatherInfo(request)));
    }

    private WeatherInfo weatherInfo(HttpServletRequest request) {
        String userIp = ipService.userIp(request);
        return weatherInfoService.currentWeatherInfo(userIp);
    }

}
