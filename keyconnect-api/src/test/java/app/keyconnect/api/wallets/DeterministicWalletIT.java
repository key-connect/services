package app.keyconnect.api.wallets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeterministicWalletIT {

  private static final Logger logger = LoggerFactory.getLogger(DeterministicWalletIT.class);

  @Test
  public void createDeterministicWalletWithEthAndXrpWallets() {
    final String passphrase = "my passphrase";
    final DeterministicWallet wallet = new DeterministicWallet(passphrase);
    final String mnemonicCode = wallet.getMnemonicCode();
    logger.info("Wallet mnemonic: {} words, {}", mnemonicCode.split(" ").length,
        mnemonicCode);
    assertThat(mnemonicCode.split(" ").length).isEqualTo(24);

    final BlockchainWalletFactory ethWalletFactory = wallet.getWalletFactory(ChainIdEnum.ETH);
    final BlockchainWalletFactory xrpWalletFactory = wallet.getWalletFactory(ChainIdEnum.XRP);

    final BlockchainWallet eth1a = ethWalletFactory.generateNext("eth1");
    assertThat(eth1a).isNotNull();
    assertThat(eth1a.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth1a.getAddress());
    final BlockchainWallet eth2a = ethWalletFactory.generateNext("eth2");
    assertThat(eth2a).isNotNull();
    assertThat(eth2a.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth2a.getAddress());

    final BlockchainWallet xrp1a = xrpWalletFactory.generateNext("xrp1");
    assertThat(xrp1a).isNotNull();
    assertThat(xrp1a.getAddress()).isNotBlank();
    logger.info("Generated XRP wallet {}", xrp1a.getAddress());
    final BlockchainWallet xrp2a = xrpWalletFactory.generateNext("xrp2");
    assertThat(xrp2a).isNotNull();
    assertThat(xrp2a.getAddress()).isNotBlank();
    logger.info("Generated XRP wallet {}", xrp2a.getAddress());

    List<BlockchainWallet> ethWallets = ethWalletFactory.getGeneratedWallets();
    assertThat(ethWallets).hasSize(2);
    ethWalletFactory.deleteLast();
    ethWallets = ethWalletFactory.getGeneratedWallets();
    assertThat(ethWallets).hasSize(1);

    List<BlockchainWallet> xrpWallets = xrpWalletFactory.getGeneratedWallets();
    assertThat(xrpWallets).hasSize(2);
    xrpWalletFactory.deleteLast();
    xrpWallets = xrpWalletFactory.getGeneratedWallets();
    assertThat(xrpWallets).hasSize(1);

    final DeterministicWallet recoveredWallet = new DeterministicWallet(passphrase, mnemonicCode,
        System.currentTimeMillis());
    final BlockchainWalletFactory rEthWalletFactory = recoveredWallet
        .getWalletFactory(ChainIdEnum.ETH);
    final BlockchainWalletFactory rXrpWalletFactory = recoveredWallet
        .getWalletFactory(ChainIdEnum.XRP);

    final BlockchainWallet eth1b = rEthWalletFactory.generateNext("eth1");
    final BlockchainWallet eth2b = rEthWalletFactory.generateNext("eth2");

    assertThat(eth1b.getAddress()).isEqualTo(eth1a.getAddress());
    assertThat(eth2b.getAddress()).isEqualTo(eth2a.getAddress());

    final BlockchainWallet xrp1b = rXrpWalletFactory.generateNext("xrp1");
    final BlockchainWallet xrp2b = rXrpWalletFactory.generateNext("xrp2");

    assertThat(xrp1b.getAddress()).isEqualTo(xrp1a.getAddress());
    assertThat(xrp2b.getAddress()).isEqualTo(xrp2a.getAddress());
  }

  @Test
  public void recoverWithJustMnemonic() throws Exception {
    final DeterministicWallet wallet = new DeterministicWallet("");
    final String mnemonic = wallet.getMnemonicCode();
    final BlockchainWallet ethWallet = wallet.getWalletFactory(ChainIdEnum.ETH).generateNext("ethwallet");
    final BlockchainWallet xrpWallet = wallet.getWalletFactory(ChainIdEnum.XRP).generateNext("xrpwallet");

    // sleep 5 seconds to get different creation time seconds
    Thread.sleep(5000);
    final DeterministicWallet rWallet = new DeterministicWallet("", mnemonic, System.currentTimeMillis());
    final BlockchainWallet rEthWallet = rWallet.getWalletFactory(ChainIdEnum.ETH).generateNext("ethwallet");
    final BlockchainWallet rXrpWallet = rWallet.getWalletFactory(ChainIdEnum.XRP).generateNext("xrpwallet");

    assertThat(rEthWallet.getAddress()).isEqualTo(ethWallet.getAddress());
    assertThat(rXrpWallet.getAddress()).isEqualTo(xrpWallet.getAddress());

  }
}
