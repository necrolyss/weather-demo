package lv.dev.mintos.weatherdemo.repository;

import lv.dev.mintos.weatherdemo.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findLocationByCityAndCountry(String city, String country);

}
