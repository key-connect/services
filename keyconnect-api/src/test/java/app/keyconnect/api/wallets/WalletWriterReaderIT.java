package app.keyconnect.api.wallets;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.io.WalletReader;
import app.keyconnect.api.wallets.io.WalletWriter;
import java.io.File;
import java.util.Arrays;
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
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext(),
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext(),
        wallet.getWalletFactory(ChainIdEnum.ETH).generateNext(),
        wallet.getWalletFactory(ChainIdEnum.XRP).generateNext(),
        wallet.getWalletFactory(ChainIdEnum.XRP).generateNext()
    );
    assertThat(wallets).hasSize(5);

    final WalletWriter writer = new WalletWriter(wallet);

    final File testFile = File.createTempFile("kc-temp", "-wallet-file");
    writer.writeToFile(testFile, "encryptionPassphrase");
    assertThat(testFile).exists();
    logger.info("Wrote to wallet file {}", testFile.getAbsolutePath());
    logger.info("Testing read...");
    final DeterministicWallet rWallet = WalletReader.fromFile(testFile);
  }
}
