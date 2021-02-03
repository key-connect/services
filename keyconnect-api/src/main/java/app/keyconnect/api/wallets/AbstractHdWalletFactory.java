package app.keyconnect.api.wallets;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;

public abstract class AbstractHdWalletFactory implements
    BlockchainWalletFactory {

  private final List<BlockchainWallet> generatedWallets = new LinkedList<>();
  private final DeterministicWallet deterministicWallet;

  public AbstractHdWalletFactory(DeterministicWallet deterministicWallet) {
    this.deterministicWallet = deterministicWallet;
  }

  @Override
  public BlockchainWallet generateNext() {
    final int thisAccountNumber = generatedWallets.size(); // zero based
    final List<ChildNumber> keyPath = deterministicWallet
        .buildPath(getChainIndex(), String.valueOf(thisAccountNumber));
    final DeterministicKey key = deterministicWallet.getChain()
        .getKeyByPath(keyPath, true);
    final BlockchainWallet ethWallet = buildWalletFromPrivateKey(key.getPrivKey());
    generatedWallets.add(ethWallet);
    return ethWallet;
  }

  @Override
  public List<BlockchainWallet> getGeneratedWallets() {
    return generatedWallets;
  }

  @Override
  public void deleteLast() {
    if (generatedWallets.size() > 0) {
      generatedWallets.remove(generatedWallets.size() - 1);
    }
  }

  abstract BlockchainWallet buildWalletFromPrivateKey(BigInteger privateKey);

  abstract String getChainIndex();
}
