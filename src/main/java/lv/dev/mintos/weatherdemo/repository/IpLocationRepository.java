package lv.dev.mintos.weatherdemo.repository;

import lv.dev.mintos.weatherdemo.model.IpLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpLocationRepository extends JpaRepository<IpLocation, String> {
}
