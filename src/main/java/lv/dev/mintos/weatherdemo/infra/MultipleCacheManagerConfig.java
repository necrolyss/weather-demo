package lv.dev.mintos.weatherdemo.infra;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lv.dev.mintos.weatherdemo.service.location.LocationService;
import lv.dev.mintos.weatherdemo.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

    @Value("${app.cache.location.expire-min}")
    private Integer locationCacheExpire;
    @Value("${app.cache.location.size}")
    private Integer locationCacheSize;

    @Value("${app.cache.weather.expire-min}")
    private Integer weatherCacheExpire;
    @Value("${app.cache.weather.size}")
    private Integer weatherCacheSize;

    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(
                buildLocationCache(ticker),
                buildWeatherCache(ticker))
        );
        return manager;
    }

    private CaffeineCache buildLocationCache(Ticker ticker) {
        return new CaffeineCache(
                LocationService.CACHE_NAME,
                Caffeine.newBuilder()
                        .expireAfterWrite(locationCacheExpire, TimeUnit.MINUTES)
                        .maximumSize(locationCacheSize)
                        .ticker(ticker)
                        .build()
        );
    }

    private CaffeineCache buildWeatherCache(Ticker ticker) {
        return new CaffeineCache(
                WeatherService.CACHE_NAME,
                Caffeine.newBuilder()
                        .expireAfterWrite(weatherCacheExpire, TimeUnit.MINUTES)
                        .maximumSize(weatherCacheSize)
                        .ticker(ticker)
                        .build()
        );
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
