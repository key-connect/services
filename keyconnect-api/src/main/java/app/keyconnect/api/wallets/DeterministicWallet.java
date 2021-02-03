package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
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
  private final Map<String, BlockchainWalletFactory> factoryMap = new ConcurrentHashMap<>(2);

  public DeterministicWallet(@Nullable String passphrase) {
    seed = new DeterministicSeed(
        new SecureRandom(),
        128,
        Optional.ofNullable(passphrase).orElse("")
    );
    chain = DeterministicKeyChain.builder().seed(seed).build();
  }

  public DeterministicWallet(@Nullable String passphrase, String mnemonicString,
      long creationTimeInSeconds) {
    try {
      seed = new DeterministicSeed(
          mnemonicString,
          null,
          Optional.ofNullable(passphrase).orElse(""),
          creationTimeInSeconds
      );
    } catch (UnreadableWalletException e) {
      throw new RuntimeException("Wallet unreadable!", e);
    }
    chain = DeterministicKeyChain.builder().seed(seed).build();
  }

  public BlockchainWalletFactory getWalletFactory(ChainIdEnum chainId) {
    switch (chainId) {
      case ETH:
        return factoryMap.computeIfAbsent(chainId.getValue(), c -> new EthHdWalletFactory(this));
      case XRP:
        return factoryMap.computeIfAbsent(chainId.getValue(), c -> new XrpHdWalletFactory(this));
      default:
        throw new NotImplementedException(
            "Wallet factory for blockchain " + chainId.getValue() + " is not yet implemented");
    }
  }

  public List<ChildNumber> buildPath(String coinType, String account) {
    return HDUtils.parsePath(String.format("M/44H/%sH/%sH/0/0", coinType, account));
  }

  public DeterministicKeyChain getChain() {
    return chain;
  }

  public String getMnemonicCode() {
    return Strings.join(this.seed.getMnemonicCode(), " ");
  }

  public DeterministicSeed getDeterministicSeed() {
    return this.seed;
  }
}
