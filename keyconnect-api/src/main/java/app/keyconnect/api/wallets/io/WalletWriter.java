package app.keyconnect.api.wallets.io;

import app.keyconnect.api.wallets.BlockchainWalletFactory;
import app.keyconnect.api.wallets.DeterministicWallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;

public class WalletWriter {

  private final DeterministicWallet wallet;

  public WalletWriter(DeterministicWallet wallet) {
    this.wallet = wallet;
  }

  public void writeToFile(File file, @Nullable String encryptionPassphrase) {
    final Optional<String> optionalEncryptionPassphrase = Optional.ofNullable(encryptionPassphrase);
    verifyFile(file);
    final WalletFile walletFile = new WalletFile(wallet.getMnemonicCode(), wallet.getPassphrase());
    wallet.getAllFactories()
        .stream()
        .filter(BlockchainWalletFactory::hasWallets)
        .forEachOrdered(f -> walletFile.getAccountIndices()
            .put(
                f.getChainIndex(),
                String.valueOf(f.getGeneratedWallets().size())
            )
        );
    String walletFileString = serializeWalletFileToString(walletFile);
    System.out.println(walletFileString);
    try {
      final BufferedOutputStream outputStream = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
      outputStream.write(walletFileString.getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      throw new WalletWriterException("Unable to write to file", e);
    }
  }

  private String serializeWalletFileToString(WalletFile walletFile) {
    try {
      return new ObjectMapper(new YAMLFactory()).writeValueAsString(walletFile);
    } catch (JsonProcessingException e) {
      throw new WalletWriterException("Failed to serialize wallet", e);
    }
  }

  private void verifyFile(File file) {
    if (file.isDirectory()) {
      file = new File(file.getAbsolutePath() + File.separator + ".wallet");
    }

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new WalletWriterException("Unable to create wallet file at " + file.getAbsolutePath(),
            e);
      }
    }
  }
}
