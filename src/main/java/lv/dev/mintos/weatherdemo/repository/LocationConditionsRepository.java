package lv.dev.mintos.weatherdemo.repository;

import lv.dev.mintos.weatherdemo.model.LocationConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationConditionsRepository extends JpaRepository<LocationConditions, Long> {
}
