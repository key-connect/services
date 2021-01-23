package app.keyconnect.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class SpringCacheConfiguration extends CachingConfigurerSupport {

  private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);

  @Override
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> {
      final String key = target.getClass().getSimpleName()
          + "//"
          + method.getName()
          + "//"
          + StringUtils.arrayToCommaDelimitedString(params);
      logger.debug("Cache key is {}", key);
      return key;
    };
  }
}
