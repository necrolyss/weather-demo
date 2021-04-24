package lv.dev.mintos.weatherdemo.service.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lv.dev.mintos.weatherdemo.infra.RestException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class WeatherInfoUnmarshaller {

    private static final String MALFORMED_JSON_MESSAGE_ID = "exception.weather-service.response.malformed";
    private static final int RESPONSE_OK_CODE = 200;

    Optional<WeatherInfo> toWeatherInfo(String rawResponse) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(rawResponse);
            if (jsonNode.at("/cod").intValue() != RESPONSE_OK_CODE) {
                return Optional.empty();
            } else {
                return Optional.of(new WeatherInfo(
                        jsonNode.at("/weather/0/description").textValue(),
                        jsonNode.at("/main/temp").doubleValue(),
                        jsonNode.at("/main/feels_like").doubleValue(),
                        jsonNode.at("/main/humidity").doubleValue(),
                        jsonNode.at("/wind/speed").doubleValue(),
                        jsonNode.at("/wind/deg").intValue()
                ));
            }
        } catch (JsonProcessingException e) {
            throw new RestException(MALFORMED_JSON_MESSAGE_ID);
        }
    }

}
