package app.keyconnect.api.wallets;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.api.client.AccountsApi;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import java.math.BigDecimal;
import org.junit.Test;

public class XrpBlockchainWalletTest {

  @Test
  public void walletGenerateAndFundInTestNet() throws Exception {
    final BlockchainWallet wallet = new XrpBlockchainWallet();
    final String address = wallet.getAddress();
    assertThat(address).isNotNull();

    wallet.fundWallet();

    final BlockchainAccountInfo accountInfo = new AccountsApi()
        .getAccountInfo("xrp", address, "testnet");
    final BigDecimal balance = new BigDecimal(accountInfo.getBalance().getAmount());
    assertThat(balance).isEqualTo(new BigDecimal("1000"));
  }

  @Test
  public void walletGenerateFromSeed() {
    BlockchainWallet wallet = new XrpBlockchainWallet();
    final String address = wallet.getAddress();
    final String seed = wallet.getSeed();
    assertThat(address).isNotBlank();
    assertThat(seed).isNotBlank();
    assertThat(address).isNotEqualTo(seed);

    wallet = new XrpBlockchainWallet(seed);
    assertThat(wallet.getAddress()).isEqualTo(address);
    assertThat(wallet.getSeed()).isEqualTo(seed);
  }
}
