package app.keyconnect.api.wallets;

import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.SeedWalletGenerationResult;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

public class XrpBlockchainWallet implements BlockchainWallet {

  private final FaucetClient faucetClient =
      FaucetClient.construct(HttpUrl.parse("https://faucet.altnet.rippletest.net"));
  private final WalletFactory walletFactory = DefaultWalletFactory.getInstance();
  private final String seed;
  private final Wallet wallet;

  public XrpBlockchainWallet() {
    final SeedWalletGenerationResult generationResult = walletFactory.randomWallet(false);
    this.seed = generationResult.seed();
    this.wallet = generationResult.wallet();
  }

  public XrpBlockchainWallet(String seed) {
    this.wallet = walletFactory.fromSeed(seed, false);
    this.seed = seed;
  }

  /**
   * Funds a wallet in testnet environment
   */
  @Override
  public void fundWallet() {
    faucetClient.fundAccount(FundAccountRequest.of(wallet.classicAddress()));
  }

  @Override
  public String getSeed() {
    return seed;
  }

  @Override
  public String getAddress() {
    return wallet.xAddress().value();
  }
}
