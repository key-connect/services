package app.keyconnect.server.utils.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtherscanAccountTransaction {

  private String timeStamp;
  private String hash;
  private String from;
  private String to;
  private String value;
  private String gas;
  private String gasPrice;
  private String gasUsed;

}
