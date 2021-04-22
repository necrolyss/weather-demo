package lv.dev.mintos.weatherdemo.service.weather;

import lv.dev.mintos.weatherdemo.infra.RestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class WeatherInfoUnmarshallerTest {

    public static final String CONSISTENT_JSON = """
            {
              "coord": {
                "lon": 24.0833,
                "lat": 57
              },
              "weather": [
                {
                  "description": "clear sky"
                }
              ],
              "base": "stations",
              "main": {
                "temp": 19,
                "feels_like": 17.74,
                "humidity": 30
              },
              "wind": {
                "speed": 5.66,
                "deg": 350
              },
              "cod": 200
            }
            """;

    public static final String ERROR_JSON = """
            {
               "cod": "404",
               "message": "city not found"
             }
            """;

    public static final String MALFORMED_JSON = """
            {
               {
            }
            """;

    @InjectMocks
    private WeatherInfoUnmarshaller weatherInfoUnmarshaller;

    @Test
    void hasConsistentWeatherInfo() {
        WeatherInfo expectedInfo = new WeatherInfo("clear sky", 19d, 17.74d, 30d, 5.66d, 350, 57d, 24.0833);

        Optional<WeatherInfo> weatherInfo = weatherInfoUnmarshaller.toWeatherInfo(CONSISTENT_JSON);

        assertThat(weatherInfo).isEqualTo(Optional.of(expectedInfo));
    }

    @Test
    void handlesErrorResponse() {
        Optional<WeatherInfo> weatherInfo = weatherInfoUnmarshaller.toWeatherInfo(ERROR_JSON);

        assertThat(weatherInfo).isNotPresent();
    }

    @Test
    void catchesMalformedResponse(){
        assertThatThrownBy(() -> weatherInfoUnmarshaller.toWeatherInfo(MALFORMED_JSON)).isInstanceOf(RestException.class);
    }

}