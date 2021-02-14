package app.keyconnect.sdk.wallets;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.sdk.wallets.io.WalletReader;
import app.keyconnect.sdk.wallets.io.WalletWriter;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletWriterReaderIT {

  private static final Logger logger = LoggerFactory.getLogger(WalletWriterReaderIT.class);

  @Test
  public void writeAndReadWalletFile() throws Exception {
    final DeterministicWallet wallet = new DeterministicWallet("my passphrase!");
    final List<BlockchainWallet> wallets = Arrays.asList(
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext("eth1"),
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext("eth2"),
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext("eth3"),
        wallet.getWalletFactory(ChainIdEnum.XRP).generateNext("xrp1"),
        wallet.getWalletFactory(ChainIdEnum.XRP).generateNext("xrp2")
    );
    assertThat(wallets).hasSize(5);

    final WalletWriter writer = new WalletWriter(wallet);

    final File testFile = File.createTempFile("kc-temp", "-wallet-file");
    writer.writeToFile(testFile, "encryptionPassphrase");
    assertThat(testFile).exists();
    logger.info("Wrote to wallet file {}", testFile.getAbsolutePath());
    logger.info("Testing read...");
    final DeterministicWallet rWallet = WalletReader.fromFile(testFile, "encryptionPassphrase");
    final List<BlockchainWallet> rEthWallets = rWallet.getWalletFactory(ChainIdEnum.ETH)
        .getGeneratedWallets();
    assertThat(rEthWallets).hasSize(3);

    final List<BlockchainWallet> rXrpWallets = rWallet.getWalletFactory(ChainIdEnum.XRP)
        .getGeneratedWallets();
    assertThat(rXrpWallets).hasSize(2);

    final List<BlockchainWallet> allWallets = new LinkedList<>();
    allWallets.addAll(rEthWallets);
    allWallets.addAll(rXrpWallets);

    assertThat(allWallets.size()).isEqualTo(wallets.size());

    for (int i = 0; i < wallets.size(); i++) {
      final BlockchainWallet originalWallet = wallets.get(i);
      final BlockchainWallet recoveredWallet = allWallets.get(i);
      assertThat(originalWallet.getName()).isEqualTo(recoveredWallet.getName());
      assertThat(originalWallet.getAddress()).isEqualTo(recoveredWallet.getAddress());
    }
  }
}
