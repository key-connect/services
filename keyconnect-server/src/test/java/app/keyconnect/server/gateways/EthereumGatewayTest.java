package app.keyconnect.server.gateways;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountTransaction;
import app.keyconnect.api.client.model.BlockchainAccountTransactionItem;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.CurrencyValue;
import app.keyconnect.api.client.model.CurrencyValue.CurrencyEnum;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.utils.EtherscanUtil;
import app.keyconnect.server.utils.models.EtherscanAccountTransaction;
import app.keyconnect.server.utils.models.EtherscanResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EthereumGatewayTest {

  @Autowired
  private YamlConfiguration yamlConfiguration;

  private final String testMainAddress = "0x6c8C5d80B9C9C644E342d60Cc904A9D5E3C7a8e3";
  private final String testMainTx = "0x70ba342f52b859dd0391e01c4643ca9c86cb7d8890737c19f0f91ddd30b387ef";
  private BlockchainGateway subject;
  private EtherscanUtil mockEtherscan;

  @BeforeEach
  public void setUp() throws Exception {
    mockEtherscan = mock(EtherscanUtil.class);
    subject = new EthereumGateway(yamlConfiguration, mockEtherscan);
  }

  @Test
  public void getNetworks() throws Exception {
    final BlockchainAccountInfo blockchainAccountInfo = subject.getAccount("mainnet", testMainAddress);
    System.out.println(blockchainAccountInfo);
    assertThat(blockchainAccountInfo).isNotNull();
  }

  @Test
  public void getAccount() throws Exception {
    final BlockchainAccountInfo account = subject.getAccount("mainnet", testMainAddress);
    assertThat(account).isNotNull();
    assertThat(account.getBalance()).isNotNull();
    assertThat(new BigDecimal(account.getBalance().getAmount())).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  public void getFee() throws Exception {
    final BlockchainFee blockchainFee = subject.getFee("mainnet");
    assertThat(blockchainFee).isNotNull();
    final CurrencyValue fee = blockchainFee.getFee();
    assertThat(fee).isNotNull();
    assertThat(fee.getCurrency()).isEqualTo(CurrencyEnum.GAS);
    assertThat(new BigDecimal(fee.getAmount())).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  void getTransaction() throws Exception {
    final BlockchainAccountTransaction blockchainTransaction = subject.getTransaction("mainnet", testMainTx);
    System.out.println(blockchainTransaction);
    assertThat(blockchainTransaction).isNotNull();
    assertThat(blockchainTransaction.getTransaction()).isNotNull();
    final BlockchainAccountTransactionItem transaction = blockchainTransaction.getTransaction();
    assertThat(transaction.getSourceAccount()).isEqualTo("0xccd77974499731bdb46b187fdf28b901fc2253ba");
    assertThat(transaction.getDestinationAccount()).isEqualTo("0x95f4b3decec688e5d9192e717757f791e11c8c9e");
    assertThat(transaction.getFee().getAmount()).isEqualTo("21000");
    assertThat(transaction.getFee().getCurrency()).isEqualTo(CurrencyEnum.GAS);
    assertThat(transaction.getAmount().getCurrency()).isEqualTo(CurrencyEnum.ETH);
    assertThat(transaction.getAmount().getAmount()).isEqualTo("0.016060534000000000");
    assertThat(transaction.getHash()).isEqualTo("0x70ba342f52b859dd0391e01c4643ca9c86cb7d8890737c19f0f91ddd30b387ef");
    assertThat(transaction.getType()).isEqualTo("transaction");
    assertThat(transaction.getStatus()).isEqualTo("ok");
  }

  @Test
  void getTransactions() throws Exception {
    final EtherscanAccountTransaction fakeTransaction = EtherscanAccountTransaction.builder()
        .from(testMainAddress)
        .to("to-addr")
        .gas("21000")
        .gasPrice("21000")
        .gasUsed("12000")
        .hash("some-hash")
        .timeStamp(String.valueOf(System.currentTimeMillis()))
        .value("999999999999")
        .build();

    final EtherscanResponse fakeResponse = EtherscanResponse.builder()
        .result(new EtherscanAccountTransaction[] {fakeTransaction})
        .build();
    doReturn(fakeResponse).when(mockEtherscan).getTransactionsForAccount(eq("mainnet"), eq(testMainAddress), any(), eq("1"), eq("5"));

    final BlockchainAccountTransactions transactions = subject
        .getTransactions(testMainAddress, "mainnet", 5, null);
    assertThat(transactions.getAccountId()).isEqualTo(testMainAddress);
    assertThat(transactions.getTransactions()).hasSize(1);
  }
}
