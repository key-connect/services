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
    final BlockchainWalletFactory ethWalletFactory = wallet.getWalletFactory(ChainIdEnum.ETH);
    final BlockchainWalletFactory xrpWalletFactory = wallet.getWalletFactory(ChainIdEnum.XRP);

    final BlockchainWallet eth1 = ethWalletFactory.generateNext();
    assertThat(eth1).isNotNull();
    assertThat(eth1.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth1.getAddress());
    final BlockchainWallet eth2 = ethWalletFactory.generateNext();
    assertThat(eth2).isNotNull();
    assertThat(eth2.getAddress()).isNotBlank();
    logger.info("Generated ETH wallet {}", eth2.getAddress());

    final BlockchainWallet xrp1 = xrpWalletFactory.generateNext();
    assertThat(xrp1).isNotNull();
    assertThat(xrp1.getAddress()).isNotBlank();
    logger.info("Generated XRP wallet {}", xrp1.getAddress());
    final BlockchainWallet xrp2 = xrpWalletFactory.generateNext();
    assertThat(xrp2).isNotNull();
    assertThat(xrp2.getAddress()).isNotBlank();
    logger.info("Generated XRP wallet {}", xrp2.getAddress());

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
  }
}
