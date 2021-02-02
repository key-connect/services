package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.utils.Strings;

public class DeterministicWalletFactory {

  private final DeterministicSeed seed;
  private final DeterministicKeyChain chain;
  private final List<ChildNumber> keyPath;
  private final DeterministicKey key;
  private final BigInteger privKey;

  public DeterministicWalletFactory() {
    seed = new DeterministicSeed(new SecureRandom().generateSeed(2048), "",
        (int) (System.currentTimeMillis() / 1000));
    chain = DeterministicKeyChain.builder().seed(seed).build();
    keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
    key = chain.getKeyByPath(keyPath, true);
    privKey = key.getPrivKey();
  }

  public DeterministicWalletFactory(String mnemonicString, long creationTimeInSeconds) {
    try {
      seed = new DeterministicSeed(mnemonicString, null, "", creationTimeInSeconds);
    } catch (UnreadableWalletException e) {
      throw new RuntimeException("Wallet unreadable!", e);
    }
    chain = DeterministicKeyChain.builder().seed(seed).build();
    keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
    key = chain.getKeyByPath(keyPath, true);
    privKey = key.getPrivKey();
  }

  public void toFile() {
    try {
      final Path kcHomePath = Files
          .createDirectories(Path.of(FileUtils.getUserDirectoryPath(), ".kc"));
      final File seedFile = Files.createFile(Path.of(kcHomePath.toString(), ".seed")).toFile();
      seedFile.createNewFile(); // create if not already there
      final PrintWriter seedWriter = new PrintWriter(seedFile);
      seedWriter.println(Strings.join(seed.getMnemonicCode(), " "));
      seedWriter.println(seed.getCreationTimeSeconds());
    } catch (IOException e) {
      throw new RuntimeException("Unable to sync seed to file", e);
    }
  }

  public static DeterministicWalletFactory fromFile() {
    try {
      final Path kcHomePath = Files
          .createDirectories(Path.of(FileUtils.getUserDirectoryPath(), ".kc"));
      final File seedFile = Files.createFile(Path.of(kcHomePath.toString(), ".seed")).toFile();
      if (!seedFile.exists()) {
        throw new RuntimeException("Seed file " + seedFile + " does not exist!");
      }

      final Scanner reader = new Scanner(seedFile);
      final String seedString = reader.nextLine();
      final long creationTime = Long.parseLong(reader.nextLine());
      return new DeterministicWalletFactory(seedString, creationTime);
    } catch (IOException e) {
      throw new RuntimeException("Unable to sync seed to file", e);
    }
  }

  public BlockchainWallet getWallet(ChainIdEnum chainId) {
    switch (chainId) {
      case ETH:
        return new EthWallet(this);
      case XRP:
        return new XrpBlockchainWallet(this);
    }
    return null;
  }

  public static DeterministicWalletFactory fromSeed(ChainIdEnum chainId, String seed) {
    return null;
  }

  public BigInteger getPrivKey() {
    return privKey;
  }

  public String getMnemonicCode() {
    return Strings.join(this.seed.getMnemonicCode(), " ");
  }

  public byte[] getSeed() {
    return seed.getSeedBytes();
  }
}
