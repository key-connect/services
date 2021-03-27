package app.keyconnect.server.factories.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YamlConfiguration {

  private List<BlockchainsConfiguration> blockchains;
  private List<MarketConfiguration> markets;

}
