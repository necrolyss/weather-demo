package lv.dev.mintos.weatherdemo.service;

import lv.dev.mintos.weatherdemo.model.Location;
import lv.dev.mintos.weatherdemo.model.LocationConditions;
import lv.dev.mintos.weatherdemo.repository.LocationConditionsRepository;
import lv.dev.mintos.weatherdemo.service.location.LocationService;
import lv.dev.mintos.weatherdemo.service.weather.WeatherInfo;
import lv.dev.mintos.weatherdemo.service.weather.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class WeatherInfoServiceTest {

    private static final String IP_ADDRESS = "123.123.123.123";
    private static final Double TEMPERATURE = 8.1;
    private static final Double FEELS_LIKE = 6.4;
    private static final Double HUMIDITY = 61d;
    private static final Double WIND_SPEED = 2.57;
    private static final Integer WIND_DIRECTION = 300;
    private static final Double LATITUDE = 56.96017074584961;
    private static final Double LONGITUDE = 24.134309768676758;
    private static final String DESCRIPTION = "Clear Sky";

    @Mock
    private LocationService locationService;
    @Mock
    private WeatherService weatherService;
    @Mock
    private LocationConditionsRepository locationConditionsRepository;
    @Captor
    private ArgumentCaptor<LocationConditions> locationConditionsCaptor;

    @InjectMocks
    private WeatherInfoService weatherInfoService;

    @Test
    void getsCurrentWeatherInfo() {
        WeatherInfo weatherInfo = prepareWeatherInfo();
        Location location = new Location("Riga", "Latvia", LATITUDE, LONGITUDE);
        given(locationService.locationOf(IP_ADDRESS)).willReturn(location);
        given(weatherService.currentWeather(location)).willReturn(weatherInfo);

        WeatherInfo actualInfo = weatherInfoService.currentWeatherInfo(IP_ADDRESS);

        verify(locationConditionsRepository).save(locationConditionsCaptor.capture());
        assertThat(actualInfo).isEqualTo(weatherInfo);
        assertThat(locationConditionsCaptor.getValue())
                .returns(TEMPERATURE, LocationConditions::getTemperature)
                .returns(FEELS_LIKE, LocationConditions::getFeelsLike)
                .returns(HUMIDITY, LocationConditions::getHumidity)
                .returns(WIND_SPEED, LocationConditions::getWindSpeed)
                .returns(WIND_DIRECTION, LocationConditions::getWindDirection)
                .returns(DESCRIPTION, LocationConditions::getDescription);

    }

    private WeatherInfo prepareWeatherInfo() {
        return new WeatherInfo(
                DESCRIPTION,
                TEMPERATURE,
                FEELS_LIKE,
                HUMIDITY,
                WIND_SPEED,
                WIND_DIRECTION
        );
    }

}