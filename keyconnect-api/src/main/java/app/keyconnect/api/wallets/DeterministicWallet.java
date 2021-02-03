package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.utils.Strings;

// https://github.com/satoshilabs/slips/blob/master/slip-0044.md
// https://medium.com/myetherwallet/hd-wallets-and-derivation-paths-explained-865a643c7bf2
public class DeterministicWallet {

  private final DeterministicSeed seed;
  private final DeterministicKeyChain chain;
  private BlockchainWalletFactory ethWalletFactory;
  private BlockchainWalletFactory xrpWalletFactory;

  public DeterministicWallet() {
    seed = new DeterministicSeed(new SecureRandom().generateSeed(256), "",
        (int) (System.currentTimeMillis() / 1000));
    chain = DeterministicKeyChain.builder().seed(seed).build();
  }

  public DeterministicWallet(String mnemonicString, long creationTimeInSeconds) {
    try {
      seed = new DeterministicSeed(mnemonicString, null, "", creationTimeInSeconds);
    } catch (UnreadableWalletException e) {
      throw new RuntimeException("Wallet unreadable!", e);
    }
    chain = DeterministicKeyChain.builder().seed(seed).build();
  }

  public static DeterministicWallet fromFile() {
    try {
      final Path kcHomePath = Files
          .createDirectories(Paths.get(FileUtils.getUserDirectoryPath(), ".kc"));
      final File seedFile = Files.createFile(Paths.get(kcHomePath.toString(), ".seed")).toFile();
      if (!seedFile.exists()) {
        throw new RuntimeException("Seed file " + seedFile + " does not exist!");
      }

      final Scanner reader = new Scanner(seedFile);
      final String seedString = reader.nextLine();
      final long creationTime = Long.parseLong(reader.nextLine());
      return new DeterministicWallet(seedString, creationTime);
    } catch (IOException e) {
      throw new RuntimeException("Unable to sync seed to file", e);
    }
  }

  public BlockchainWalletFactory getWalletFactory(ChainIdEnum chainId) {
    switch(chainId) {
      case ETH:
        if (ethWalletFactory == null) ethWalletFactory = new EthHdWalletFactory(this);
        return ethWalletFactory;
      case XRP:
        if (xrpWalletFactory == null) xrpWalletFactory = new XrpHdWalletFactory(this);
        return xrpWalletFactory;
      default:
        throw new NotImplementedException("Wallet factory for blockchain " + chainId.getValue() + " is not yet implemented");
    }
  }

  public List<ChildNumber> buildPath(String coinType, String account) {
    return HDUtils.parsePath(String.format("M/44H/%sH/%sH/0/0", coinType, account));
  }

  public void toFile() {
    try {
      final Path kcHomePath = Files
          .createDirectories(Paths.get(FileUtils.getUserDirectoryPath(), ".kc"));
      final File seedFile = Files.createFile(Paths.get(kcHomePath.toString(), ".seed")).toFile();
      seedFile.createNewFile(); // create if not already there
      final PrintWriter seedWriter = new PrintWriter(seedFile);
      seedWriter.println(Strings.join(seed.getMnemonicCode(), " "));
      seedWriter.println(seed.getCreationTimeSeconds());
    } catch (IOException e) {
      throw new RuntimeException("Unable to sync seed to file", e);
    }
  }

  public DeterministicKeyChain getChain() {
    return chain;
  }

  public String getMnemonicCode() {
    return Strings.join(this.seed.getMnemonicCode(), " ");
  }

  public byte[] getSeed() {
    return seed.getSeedBytes();
  }
}
