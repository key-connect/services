package app.keyconnect.api.wallets.io;

import app.keyconnect.api.wallets.DeterministicWallet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class WalletReader {

  public static DeterministicWallet fromFile(File file) {
    if (!file.exists() || !file.canRead()) {
      throw new WalletReaderException(
          "Cannot read wallet file, it may not exist or permissions may prevent read");
    }
    try {
      final String walletString;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(
          new GZIPInputStream(new FileInputStream(file))))) {
        final String content = reader.lines()
            .collect(Collectors.joining(System.lineSeparator()));
        walletString = new String(content);
      }
      System.out.println(walletString);
      final WalletFile walletFile = new ObjectMapper(new YAMLFactory())
          .readValue(walletString, WalletFile.class);
      return new DeterministicWallet(walletFile.getPassphrase(), walletFile.getMnemonic(),
          System.currentTimeMillis());
    } catch (IOException e) {
      throw new WalletReaderException(
          "Unable to read / parse wallet, file may be malformed or tampered with", e);
    }
  }

}
