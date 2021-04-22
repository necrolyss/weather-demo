package lv.dev.mintos.weatherdemo.service.location;

import lv.dev.mintos.weatherdemo.infra.Resilient;
import lv.dev.mintos.weatherdemo.model.IpLocation;
import lv.dev.mintos.weatherdemo.model.Location;
import lv.dev.mintos.weatherdemo.repository.IpLocationRepository;
import lv.dev.mintos.weatherdemo.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class LocationServiceTest {

    private static final String IP_ADDRESS = "123.123.123.123";
    private static final String CITY = "Riga";
    private static final String COUNTRY = "Latvia";
    public static final long LOCATION_ID = 1L;

    @Mock
    private GetLocationHandler getLocationHandler;
    @Mock
    private Resilient resilient;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private IpLocationRepository ipLocationRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void foundsLocationFromDbCache() {
        Location location = new Location(CITY, COUNTRY);
        Optional<IpLocation> ipLocation = Optional.of(getIpLocation(location));
        given(ipLocationRepository.findById(IP_ADDRESS)).willReturn(ipLocation);

        Location foundLocation = locationService.locationOf(IP_ADDRESS);

        assertThat(foundLocation).isEqualTo(location);
    }

    @Test
    void resolvesLocationFromRemote() {
        Location location = new Location(CITY, COUNTRY);
        given(ipLocationRepository.findById(IP_ADDRESS)).willReturn(Optional.empty());
        given(getLocationHandler.handle(any())).willReturn(location);
        doAnswer(invocationOnMock -> {
            GetLocationCommand command = invocationOnMock.getArgument(0);
            return getLocationHandler.handle(command);
        }).when(resilient).invoke(any(), any());
        given(locationRepository.findLocationByCityAndCountry(CITY, COUNTRY)).willReturn(Optional.of(location));

        Location foundLocation = locationService.locationOf(IP_ADDRESS);

        assertThat(foundLocation).isEqualTo(location);
        verify(locationRepository, times(0)).save(any());
    }

    @Test
    void resolvesLocationFromRemoteAndSavesToDB() {
        given(ipLocationRepository.findById(IP_ADDRESS)).willReturn(Optional.empty());
        given(getLocationHandler.handle(any())).willReturn(new Location(CITY, COUNTRY));
        doAnswer(invocationOnMock -> {
            GetLocationCommand command = invocationOnMock.getArgument(0);
            return getLocationHandler.handle(command);
        }).when(resilient).invoke(any(), any());
        given(locationRepository.findLocationByCityAndCountry(CITY, COUNTRY)).willReturn(Optional.empty());
        doAnswer((Answer<Object>) invocationOnMock -> storedLocation(invocationOnMock.getArgument(0)))
                .when(locationRepository).save(any());

        Location foundLocation = locationService.locationOf(IP_ADDRESS);

        assertThat(foundLocation)
                .returns(LOCATION_ID, Location::getId)
                .returns(CITY, Location::getCity)
                .returns(COUNTRY, Location::getCountry);
    }

    private IpLocation getIpLocation(Location location) {
        IpLocation ipLocation = new IpLocation();
        ipLocation.setIp(IP_ADDRESS);
        ipLocation.setLocation(location);
        return ipLocation;
    }

    private Location storedLocation(Location location) {
        Location storedLocation = new Location();
        storedLocation.setCity(location.getCity());
        storedLocation.setCountry(location.getCountry());
        storedLocation.setId(LOCATION_ID);
        return storedLocation;
    }
}