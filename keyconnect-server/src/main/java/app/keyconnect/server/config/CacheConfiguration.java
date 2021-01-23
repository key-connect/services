package app.keyconnect.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CacheConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);
  public static final String BEAN_FAST_CACHE = "fastCache";
  public static final String BEAN_SLOW_CACHE = "slowCache";
  public static final String BEAN_ELEPHANT_CACHE = "elephantCache";
  private static final long ELEPHANT_MAX_SIZE = 100000L;
  public static final long FAST_MAXIMUM_SIZE = 10000L;
  public static final long SLOW_MAXIMUM_SIZE = 10000L;

  @Bean(BEAN_FAST_CACHE) // 10 seconds refresh time
  @Primary
  public CacheManager fastCaffeineCacheManager() {
    final Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .maximumSize(FAST_MAXIMUM_SIZE);

    logger.info("Configuring caffeine cache manager");
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    caffeineCacheManager.setCacheNames(Arrays.asList("fast"));
    return caffeineCacheManager;
  }

  @Bean(BEAN_SLOW_CACHE) // 30 seconds refresh time
  public CacheManager slowCaffeineCacheManager() {
    final Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(SLOW_MAXIMUM_SIZE);

    logger.info("Configuring caffeine cache manager");
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    caffeineCacheManager.setCacheNames(Arrays.asList("slow"));
    return caffeineCacheManager;
  }

  @Bean(BEAN_ELEPHANT_CACHE) // 30 seconds refresh time
  public CacheManager elephantCaffeineCacheManager() {
    final Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
        .maximumSize(ELEPHANT_MAX_SIZE);

    logger.info("Configuring caffeine cache manager");
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    caffeineCacheManager.setCacheNames(Arrays.asList("elephant"));
    return caffeineCacheManager;
  }
}
