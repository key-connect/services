package app.keyconnect.rippled.api.spring;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    return objectMapper;
  }

  @Bean
  public RestOperations restOperations(
      MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
    return constructRestTemplate(mappingJackson2HttpMessageConverter);
  }

  @NotNull
  public static RestTemplate constructRestTemplate(
      MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
    final RestTemplate rest = new RestTemplate();
    for (int i = 0; i < rest.getMessageConverters().size(); i++) {
      final HttpMessageConverter<?> messageConverter = rest.getMessageConverters().get(i);
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        // we replace built-in message converter with our own
        rest.getMessageConverters().remove(i);
        rest.getMessageConverters().add(i, mappingJackson2HttpMessageConverter);
      }
    }
    return rest;
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter(
      ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    return converter;
  }

}
