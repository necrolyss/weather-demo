package lv.dev.mintos.weatherdemo.service.weather;

import lv.dev.mintos.weatherdemo.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
class GetWeatherHandlerTest {

    private static final String API_KEY = "123ABC";
    private static final String PROVIDER_URL = "http://127.0.0.1/api";
    private static final Double LATITUDE = 56.96017074584961;
    private static final Double LONGITUDE = 24.134309768676758;

    private static final String VALID_LOCATION_RESPONSE = """
            "weather": good
            """;
    private static final String INVALID_LOCATION_RESPONSE = "{}";

    private static MockedStatic<ClientBuilder> jerseyClientBuilder;

    @Mock
    private Client jerseyClient;
    @Mock
    private WebTarget webTarget;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private WeatherInfoUnmarshaller weatherInfoUnmarshaller;
    @Mock
    private WeatherInfo weatherInfo;

    private GetWeatherHandler getLocationHandler;

    @BeforeEach
    void setUp() {
        jerseyClientBuilder = mockStatic(ClientBuilder.class);
        jerseyClientBuilder.when(() -> ClientBuilder.newClient(any())).thenReturn(this.jerseyClient);
        given(jerseyClient.target(PROVIDER_URL)).willReturn(webTarget);
        given(webTarget.queryParam("q", "Riga,Latvia")).willReturn(webTarget);
        given(webTarget.queryParam("appid", API_KEY)).willReturn(webTarget);
        given(webTarget.queryParam("units", "metric")).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.accept(MediaType.APPLICATION_JSON)).willReturn(builder);

        getLocationHandler = new GetWeatherHandler(weatherInfoUnmarshaller, PROVIDER_URL, API_KEY);
    }

    @AfterEach
    void tearDown() {
        jerseyClientBuilder.close();
    }

    @Test
    void handlesCommandForValidLocation() {
        given(builder.get(String.class)).willReturn(VALID_LOCATION_RESPONSE);
        given(weatherInfoUnmarshaller.toWeatherInfo(VALID_LOCATION_RESPONSE)).willReturn(Optional.of(weatherInfo));
        GetWeatherCommand command = new GetWeatherCommand(new Location("Riga", "Latvia", LATITUDE, LONGITUDE));

        WeatherInfo actualInfo = getLocationHandler.handle(command);

        assertThat(actualInfo).isEqualTo(weatherInfo);
    }

    @Test
    void handlesCommandForInvalidLocation() {
        given(builder.get(String.class)).willReturn(INVALID_LOCATION_RESPONSE);
        given(weatherInfoUnmarshaller.toWeatherInfo(INVALID_LOCATION_RESPONSE)).willReturn(Optional.empty());
        GetWeatherCommand command = new GetWeatherCommand(new Location("Riga", "Latvia", LATITUDE, LONGITUDE));

        assertThatThrownBy(() -> getLocationHandler.handle(command))
            .isInstanceOf(WeatherUnavailable.class);
    }
}