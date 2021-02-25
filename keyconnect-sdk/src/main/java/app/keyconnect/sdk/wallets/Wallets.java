package app.keyconnect.sdk.wallets;

import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.AccountsApi;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.sdk.api.KeyConnectApiFactory;
import java.util.Locale;
import javax.annotation.Nullable;

public class Wallets {

  /**
   * Gets wallet information given a wallet and a network
   * @param wallet Blockchain wallet to get the info of
   * @param network Blockchain network (eg mainnet/testnet/ropsten etc)
   * @return BlockchainAccountInfo Account information
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountInfo getWalletInfo(BlockchainWallet wallet, String network)
      throws ApiException {
    return getWalletInfo(wallet, network, null);
  }

  /**
   * Gets wallet information given a wallet and a network
   * @param wallet Blockchain wallet to get the info of
   * @param network Blockchain network (eg mainnet/testnet/ropsten etc)
   * @param fiat Fiat to see the equivalent value
   * @return BlockchainAccountInfo Account information
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountInfo getWalletInfo(BlockchainWallet wallet, String network, @Nullable String fiat)
      throws ApiException {
    return getWalletInfo(wallet, network, fiat, KeyConnectApiFactory.getInstance()
        .getDefaultAccountsApi());
  }

  /**
   * Gets wallet information given a wallet and a network
   * @param wallet Blockchain wallet to get the info of
   * @param network Blockchain network (eg mainnet/testnet/ropsten etc)
   * @param fiat Fiat to see the equivalent value
   * @param accountsApi Accounts API object to use to connect to the server
   * @return BlockchainAccountInfo Account information
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountInfo getWalletInfo(BlockchainWallet wallet, String network, @Nullable String fiat, AccountsApi accountsApi)
      throws ApiException {
    return accountsApi.getAccountInfo(wallet.getChainId().name().toLowerCase(Locale.ROOT), wallet.getAddress(), network, fiat);
  }

}
