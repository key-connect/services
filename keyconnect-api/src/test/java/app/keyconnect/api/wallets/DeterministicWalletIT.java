package app.keyconnect.api.wallets;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeterministicWalletIT {

  private static final Logger logger = LoggerFactory.getLogger(DeterministicWalletIT.class);
  @Test
  public void createDeterministicWalletWithEthAndXrpWallets() {
    final DeterministicWallet wallet = new DeterministicWallet();
    final String mnemonicCode = wallet.getMnemonicCode();
    final long creationTimeSeconds = wallet.getDeterministicSeed().getCreationTimeSeconds();
    logger.info("Wallet mnemonic: {} words, {}", mnemonicCode.split(" ").length,
        mnemonicCode);

    final BlockchainWalletFactory ethWalletFactory = wallet.getWalletFactory(ChainIdEnum.ETH);
    final BlockchainWalletFactory xrpWalletFactory = wallet.getWalletFactory(ChainIdEnum.XRP);

    final BlockchainWallet eth1a = ethWalletFactory.generateNext();
    assertThat(eth1a).isNotNull();
    assertThat(eth1a.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth1a.getAddress());
    final BlockchainWallet eth2a = ethWalletFactory.generateNext();
    assertThat(eth2a).isNotNull();
    assertThat(eth2a.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth2a.getAddress());

    final BlockchainWallet xrp1a = xrpWalletFactory.generateNext();
    assertThat(xrp1a).isNotNull();
    assertThat(xrp1a.getAddress()).isNotBlank();
    logger.info("Generated XRP wallet {}", xrp1a.getAddress());
    final BlockchainWallet xrp2a = xrpWalletFactory.generateNext();
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

    final DeterministicWallet recoveredWallet = new DeterministicWallet(mnemonicCode, creationTimeSeconds);
    final BlockchainWalletFactory rEthWalletFactory = recoveredWallet.getWalletFactory(ChainIdEnum.ETH);
    final BlockchainWalletFactory rXrpWalletFactory = recoveredWallet.getWalletFactory(ChainIdEnum.XRP);

    final BlockchainWallet eth1b = rEthWalletFactory.generateNext();
    final BlockchainWallet eth2b = rEthWalletFactory.generateNext();

    assertThat(eth1b.getAddress()).isEqualTo(eth1a.getAddress());
    assertThat(eth2b.getAddress()).isEqualTo(eth2a.getAddress());

    final BlockchainWallet xrp1b = rXrpWalletFactory.generateNext();
    final BlockchainWallet xrp2b = rXrpWalletFactory.generateNext();

    assertThat(xrp1b.getAddress()).isEqualTo(xrp1a.getAddress());
    assertThat(xrp2b.getAddress()).isEqualTo(xrp2a.getAddress());
  }
}
