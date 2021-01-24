package app.keyconnect.server.services.rate;

import app.keyconnect.rippled.api.spring.JacksonConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(JacksonConfig.class)
public class RateConfig {

  public static final String BEAN_RATE_SERVICE_REST_TEMPLATE = "rateServiceRestTemplate";

  @Bean(BEAN_RATE_SERVICE_REST_TEMPLATE)
  public RestTemplate rateServiceRestTemplate(
      MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
    return JacksonConfig.constructRestTemplate(mappingJackson2HttpMessageConverter);
  }

  @Bean
  public RateService rateService(
      @Qualifier(BEAN_RATE_SERVICE_REST_TEMPLATE) RestTemplate rateServiceRestTemplate) {
    return new CryptonatorRateService(rateServiceRestTemplate);
  }

}
