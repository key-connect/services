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
  private String contractAddress;
  private String tokenName;
  private String tokenSymbol;
  private String tokenDecimal;
  private String confirmations;
  private String blockNumber;
  private String blockHash;

}
