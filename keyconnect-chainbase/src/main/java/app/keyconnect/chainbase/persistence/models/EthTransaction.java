package app.keyconnect.chainbase.persistence.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@Entity(name = "eth_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(columnList = "hash"),
    @Index(columnList = "from_account"),
    @Index(columnList = "to_account"),
    @Index(columnList = "from_account,to_account"),
    @Index(columnList = "timestamp"),
})
public class EthTransaction {

  @Id
  @Column(name = "hash")
  private String hash;

  @Column(name = "block_number")
  private long blockNumber;

  @Column(name = "from_account")
  private String from;

  @Column(name = "to_account")
  private String to;

  @Column(name = "tx_value")
  private String value;

  @Column(name = "gas_limit")
  private String gasLimit;

  @Column(name = "gas_price")
  private String gasPrice;

  @Column(name = "nonce")
  private String nonce;

  @Column(name = "input", columnDefinition = "TEXT")
  private String input;

  @Column(name = "timestamp")
  private String timestamp;

}
