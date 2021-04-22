package lv.dev.mintos.weatherdemo.service.weather;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lv.dev.mintos.weatherdemo.model.Location;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.StringJoiner;

@Component
public class GetWeatherHandler implements Command.Handler<GetWeatherCommand, WeatherInfo>{

    private static final JacksonJsonProvider JACKSON_JSON_PROVIDER = new JacksonJaxbJsonProvider()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String QUERY_PARAMETER = "q";
    private static final String ACCESS_KEY_PARAMETER = "appid";
    private static final String UNITS_PARAMETER = "units";
    private static final String UNITS_PARAMETER_VALUE = "metric";

    private final WeatherInfoUnmarshaller weatherInfoUnmarshaller;
    private final String providerUrl;
    private final String accessKey;

    @Autowired
    public GetWeatherHandler(WeatherInfoUnmarshaller weatherInfoUnmarshaller,
                             @Value("${weather.provider.url}") String providerUrl,
                             @Value("${weather.provider.api.key}") String accessKey) {
        this.weatherInfoUnmarshaller = weatherInfoUnmarshaller;
        this.providerUrl = providerUrl;
        this.accessKey = accessKey;
    }

    @Override
    public WeatherInfo handle(GetWeatherCommand getWeatherCommand) {
        Location location = getWeatherCommand.location();
        String rawResponse = getRawResponse(location);
        return weatherInfoUnmarshaller.toWeatherInfo(rawResponse)
                .orElseThrow(() -> new WeatherUnavailable(location));
    }

    private String getRawResponse(Location location) {
        Client weatherClient = createWeatherClientConfig();
        return weatherClient.target(providerUrl)
                .queryParam(QUERY_PARAMETER, locationToken(location))
                .queryParam(ACCESS_KEY_PARAMETER, accessKey)
                .queryParam(UNITS_PARAMETER, UNITS_PARAMETER_VALUE)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
    }

    private Client createWeatherClientConfig() {
        ClientConfig config = new ClientConfig();
        config.register(JACKSON_JSON_PROVIDER);
        return ClientBuilder.newClient(config);
    }

    private String locationToken(Location location) {
        return new StringJoiner(",")
                .add(location.getCity())
                .add(location.getCountry())
                .toString();
    }

}
