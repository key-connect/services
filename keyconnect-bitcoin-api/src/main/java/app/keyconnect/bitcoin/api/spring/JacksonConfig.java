package app.keyconnect.bitcoin.api.spring;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class JacksonConfig {

  @NotNull
  public static RestTemplate constructRestTemplate() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

    final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);

    final RestTemplate rest = new RestTemplate();
    for (int i = 0; i < rest.getMessageConverters().size(); i++) {
      final HttpMessageConverter<?> messageConverter = rest.getMessageConverters().get(i);
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        // we replace built-in message converter with our own
        rest.getMessageConverters().remove(i);
        rest.getMessageConverters().add(i, converter);
      }
    }

    return rest;
  }

}
