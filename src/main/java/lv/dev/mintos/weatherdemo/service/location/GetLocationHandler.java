package lv.dev.mintos.weatherdemo.service.location;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lv.dev.mintos.weatherdemo.model.Location;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Component
class GetLocationHandler implements Command.Handler<GetLocationCommand, Location> {

    private static final JacksonJsonProvider JACKSON_JSON_PROVIDER = new JacksonJaxbJsonProvider()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String ACCESS_KEY_PARAMETER = "access_key";

    private final String providerApiKey;
    private final String locationProviderUrl;

    @Autowired
    public GetLocationHandler( @Value("${location.provider.api.key}") String providerApiKey,
                               @Value("${location.provider.url}") String locationProviderUrl) {
        this.providerApiKey = providerApiKey;
        this.locationProviderUrl = locationProviderUrl;
    }

    @Override
    public Location handle(GetLocationCommand getLocationCommand) {
        return resolveLocation(getLocationCommand.userIp());
    }

    private Location resolveLocation(String ipAddress) {
        Client client = createLocationClient();
        return Optional.of(loadLocation(ipAddress, client))
                .filter(this::isValidLocation)
                .orElseThrow(() -> new UnknownLocation(ipAddress));
    }

    private Client createLocationClient() {
        ClientConfig config = new ClientConfig();
        config.register(JACKSON_JSON_PROVIDER);
        return ClientBuilder.newClient(config);
    }

    private Location loadLocation(String ipAddress, Client client) {
        return client.target(locationProviderUrl)
                .path(ipAddress)
                .queryParam(ACCESS_KEY_PARAMETER, providerApiKey)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Location.class);
    }

    private boolean isValidLocation(Location location) {
        return StringUtils.hasText(location.getCity()) && StringUtils.hasText(location.getCountry());
    }

}
