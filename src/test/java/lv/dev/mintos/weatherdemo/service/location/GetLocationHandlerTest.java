package lv.dev.mintos.weatherdemo.service.location;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
class GetLocationHandlerTest {

    private static final String API_KEY = "123ABC";
    private static final String PROVIDER_URL = "http://127.0.0.1/api";
    private static final String REQUESTER_PUBLIC_IP = "123.123.123.123";
    private static final String REQUESTER_PRIVATE_IP = "192.168.1.1";

    private static MockedStatic<ClientBuilder> jerseyClientBuilder;

    @Mock
    private Client jerseyClient;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder builder;

    private GetLocationHandler getLocationHandler;

    @BeforeEach
    void setUp() {
        jerseyClientBuilder = mockStatic(ClientBuilder.class);
        jerseyClientBuilder.when(() -> ClientBuilder.newClient(any())).thenReturn(this.jerseyClient);
        given(jerseyClient.target(PROVIDER_URL)).willReturn(webTarget);
        given(webTarget.queryParam("access_key", API_KEY)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.accept(MediaType.APPLICATION_JSON)).willReturn(builder);

        getLocationHandler = new GetLocationHandler(API_KEY, PROVIDER_URL);
    }

    @AfterEach
    void tearDown() {
        jerseyClientBuilder.close();
    }

    @Test
    void handlesCommandForValidIp() {
        given(webTarget.path(REQUESTER_PUBLIC_IP)).willReturn(webTarget);
        given(builder.get(Location.class)).willReturn(new Location("Riga", "Latvia"));

        Location location = getLocationHandler.handle(new GetLocationCommand(REQUESTER_PUBLIC_IP));

        assertThat(location).isEqualTo(new Location("Riga", "Latvia"));
    }

    @Test
    void failCommandIfInvalidIP() {
        given(webTarget.path(REQUESTER_PRIVATE_IP)).willReturn(webTarget);
        given(builder.get(Location.class)).willReturn(new Location());

        GetLocationCommand getLocationCommand = new GetLocationCommand(REQUESTER_PRIVATE_IP);
        assertThatThrownBy(() -> getLocationHandler.handle(getLocationCommand)).isInstanceOf(UnknownLocation.class);
    }

}