package app.keyconnect.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CacheConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);
  private static final long ELEPHANT_MAX_SIZE = 100000L;
  public static final long FAST_MAXIMUM_SIZE = 10000L;
  public static final long SLOW_MAXIMUM_SIZE = 10000L;

  @Bean // 10 seconds refresh time
  @Primary
  public CacheManager fastCaffeineCacheManager() {
    final Caffeine<Object, Object> fastCaffeine = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .maximumSize(FAST_MAXIMUM_SIZE);

    final Caffeine<Object, Object> slowCaffeine = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(SLOW_MAXIMUM_SIZE);

    final Caffeine<Object, Object> elephantCaffeine = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(ELEPHANT_MAX_SIZE);

    logger.info("Configuring fast caffeine cache manager");
    final CaffeineCacheManager fastCaffeineCacheManager = new CaffeineCacheManager();
    fastCaffeineCacheManager.setCaffeine(fastCaffeine);
    fastCaffeineCacheManager.setCacheNames(Collections.singletonList("fast"));

    logger.info("Configuring slow caffeine cache manager");
    final CaffeineCacheManager slowCaffeineManager = new CaffeineCacheManager();
    slowCaffeineManager.setCaffeine(slowCaffeine);
    slowCaffeineManager.setCacheNames(Collections.singletonList("slow"));

    logger.info("Configuring elephant caffeine cache manager");
    final CaffeineCacheManager elephantCaffeineManager = new CaffeineCacheManager();
    elephantCaffeineManager.setCaffeine(elephantCaffeine);
    elephantCaffeineManager.setCacheNames(Collections.singletonList("elephant"));

    return new CompositeCacheManager(fastCaffeineCacheManager, slowCaffeineManager,
        elephantCaffeineManager);
  }
}
