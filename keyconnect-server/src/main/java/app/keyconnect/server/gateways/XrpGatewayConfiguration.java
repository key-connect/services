package app.keyconnect.server.gateways;

import app.keyconnect.rippled.api.client.PublicRippledClient;
import app.keyconnect.rippled.api.spring.JacksonConfig;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.services.networks.XrpNetworkClientService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@Import({JacksonConfig.class, YamlConfiguration.class})
public class XrpGatewayConfiguration {

  private static final String BEAN_XRP_NETWORK_SERVICE = "xrpNetworkService";
  private static final String XRP_GATEWAY = "XrpGateway";

  @Bean(BEAN_XRP_NETWORK_SERVICE)
  public NetworkClientService<PublicRippledClient> xrpNetworkClientService(
      YamlConfiguration yamlConfiguration,
      MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
    return new XrpNetworkClientService(yamlConfiguration,
        () -> JacksonConfig.constructRestTemplate(mappingJackson2HttpMessageConverter));
  }

  @Bean(XRP_GATEWAY)
  public XrpGateway xrpGateway(
      @Qualifier(BEAN_XRP_NETWORK_SERVICE) NetworkClientService<PublicRippledClient> xrpNetworkClientService) {
    return new XrpGateway(xrpNetworkClientService);
  }

}
