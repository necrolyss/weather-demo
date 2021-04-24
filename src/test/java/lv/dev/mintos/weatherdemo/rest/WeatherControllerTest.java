package lv.dev.mintos.weatherdemo.rest;

import lv.dev.mintos.weatherdemo.infra.ApiResponse;
import lv.dev.mintos.weatherdemo.service.IpService;
import lv.dev.mintos.weatherdemo.service.WeatherInfoService;
import lv.dev.mintos.weatherdemo.service.weather.WeatherInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class WeatherControllerTest {

    private static final String USER_IP = "127.0.0.1";

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private IpService ipService;

    @Mock
    private WeatherInfoService weatherInfoService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void getsCurrentWeather() {
        WeatherInfo weatherInfo = new WeatherInfo("sunny", 1d, 2d, 3d, 4d, 5);
        given(ipService.userIp(httpServletRequest)).willReturn(USER_IP);
        given(weatherInfoService.currentWeatherInfo(USER_IP)).willReturn(weatherInfo);

        ResponseEntity<ApiResponse> wrapper = weatherController.currentWeather(httpServletRequest);

        assertThat(wrapper.getBody()).isEqualTo(new ApiResponse(true, weatherInfo, null));
    }
}