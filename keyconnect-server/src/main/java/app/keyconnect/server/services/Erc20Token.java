package app.keyconnect.server.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Erc20Token {

  private String contractAddress;
  private String tokenSymbol;
  private String tokenDecimal;

}
