package lv.dev.mintos.weatherdemo.service.location;

import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import lv.dev.mintos.weatherdemo.infra.Resilient;
import lv.dev.mintos.weatherdemo.model.IpLocation;
import lv.dev.mintos.weatherdemo.model.Location;
import lv.dev.mintos.weatherdemo.repository.IpLocationRepository;
import lv.dev.mintos.weatherdemo.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LocationService  {

    public static final String CACHE_NAME = "ip_location";

    private final GetLocationHandler getLocationHandler;
    private final Resilient resilient;
    private final LocationRepository locationRepository;
    private final IpLocationRepository ipLocationRepository;

    @Autowired
    public LocationService(GetLocationHandler getLocationHandler, Resilient resilient,
                           LocationRepository locationRepository, IpLocationRepository ipLocationRepository) {
        this.getLocationHandler = getLocationHandler;
        this.resilient = resilient;
        this.locationRepository = locationRepository;
        this.ipLocationRepository = ipLocationRepository;
    }

    @Cacheable(CACHE_NAME)
    public Location locationOf(String ipAddress) {
        return ipLocationRepository.findById(ipAddress)
                .map(IpLocation::getLocation)
                .orElseGet(() -> createAndGetLocation(ipAddress));
    }

    private Location createAndGetLocation(String ipAddress) {
        Location location = resolveLocation(ipAddress);
        Location cachedLocation = cachedLocationOf(location)
                .orElseGet(() -> locationRepository.save(location));
        ipLocationRepository.save(new IpLocation(ipAddress, cachedLocation));
        return cachedLocation;
    }

    private Location resolveLocation(String ipAddress) {
        Pipeline pipeline = new Pipelinr()
                .with(() -> Stream.of(getLocationHandler))
                .with(() -> Stream.of(resilient));
        return pipeline.send(new GetLocationCommand(ipAddress));
    }

    private Optional<Location> cachedLocationOf(Location location) {
        return locationRepository.findLocationByCityAndCountry(location.getCity(), location.getCountry());
    }

}
