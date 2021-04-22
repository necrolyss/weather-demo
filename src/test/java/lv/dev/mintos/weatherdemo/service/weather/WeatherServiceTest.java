package lv.dev.mintos.weatherdemo.service.weather;

import lv.dev.mintos.weatherdemo.infra.Resilient;
import lv.dev.mintos.weatherdemo.model.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(SpringExtension.class)
class WeatherServiceTest {

    @Mock
    private GetWeatherHandler getWeatherHandler;
    @Mock
    private Resilient resilient;
    @Mock
    private WeatherInfo weatherInfo;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void currentWeather() {
        given(getWeatherHandler.handle(any())).willReturn(weatherInfo);
        doAnswer(invocationOnMock -> {
            GetWeatherCommand command = invocationOnMock.getArgument(0);
            return getWeatherHandler.handle(command);
        }).when(resilient).invoke(any(), any());

        WeatherInfo actualInfo = weatherService.currentWeather(new Location("Riga", "Latvia"));

        assertThat(actualInfo).isEqualTo(weatherInfo);
    }
}