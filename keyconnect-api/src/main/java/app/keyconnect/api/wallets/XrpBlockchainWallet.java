package app.keyconnect.api.wallets;

import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.codec.addresses.Base58;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.SeedWalletGenerationResult;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

public class XrpBlockchainWallet implements BlockchainWallet {

  private final WalletFactory walletFactory = DefaultWalletFactory.getInstance();
  private Wallet wallet;
  private String seed;

  public XrpBlockchainWallet() {
    final SeedWalletGenerationResult generationResult = walletFactory.randomWallet(false);
    this.seed = generationResult.seed();
    this.wallet = generationResult.wallet();
  }

  public XrpBlockchainWallet(String seed) {
    this.wallet = walletFactory.fromSeed(seed, false);
    this.seed = seed;
  }

  public XrpBlockchainWallet(DeterministicWalletFactory deterministicWalletFactory) {
    this.seed = Base58.encode(deterministicWalletFactory.getSeed());
    this.wallet = walletFactory.fromSeed(seed, false);
  }

  @Override
  public String getSeed() {
    return seed;
  }

  @Override
  public String getAddress() {
    return wallet.classicAddress().value();
  }
}
