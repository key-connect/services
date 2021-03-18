package app.keyconnect.sdk.wallets;

import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.AccountsApi;
import app.keyconnect.api.client.TransactionsApi;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.sdk.api.KeyConnectApiFactory;
import java.util.Locale;
import javax.annotation.Nullable;

public class Wallets {

  /**
   * Gets wallet information given a wallet and a network using the default transactions API,
   * without fiat
   *
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
   * Gets wallet information given a wallet and a network using the default transactions API
   *
   * @param wallet Blockchain wallet to get the info of
   * @param network Blockchain network (eg mainnet/testnet/ropsten etc)
   * @param fiat Fiat to see the equivalent value
   * @return BlockchainAccountInfo Account information
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountInfo getWalletInfo(BlockchainWallet wallet, String network,
      @Nullable String fiat)
      throws ApiException {
    return getWalletInfo(wallet, network, fiat, KeyConnectApiFactory.getInstance()
        .getDefaultAccountsApi());
  }

  /**
   * Gets wallet information given a wallet and a network
   *
   * @param wallet Blockchain wallet to get the info of
   * @param network Blockchain network (eg mainnet/testnet/ropsten etc)
   * @param fiat Fiat to see the equivalent value
   * @param accountsApi Accounts API object to use to connect to the server
   * @return BlockchainAccountInfo Account information
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountInfo getWalletInfo(BlockchainWallet wallet, String network,
      @Nullable String fiat, AccountsApi accountsApi)
      throws ApiException {
    return accountsApi
        .getAccountInfo(wallet.getChainId().name().toLowerCase(Locale.ROOT), wallet.getAddress(),
            network, fiat);
  }

  /**
   * Gets the wallet transactions using the default transactions API, without fiat
   *
   * @param wallet Blockchain wallet
   * @param network Blockchain network
   * @param cursor Optional cursor value, used for pagination
   * @return BlockchainAccountTransactions Wallet transactions
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountTransactions getWalletTransactions(BlockchainWallet wallet,
      String network, @Nullable String cursor)
      throws ApiException {
    return getWalletTransactions(wallet, network, cursor, null);
  }

  /**
   * Gets the wallet transactions using the default transactions API
   *
   * @param wallet Blockchain wallet
   * @param network Blockchain network
   * @param cursor Optional cursor value, used for pagination
   * @param fiat Fiat to see the equivalent value
   * @return BlockchainAccountTransactions Wallet transactions
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountTransactions getWalletTransactions(BlockchainWallet wallet,
      String network, @Nullable String cursor, @Nullable String fiat)
      throws ApiException {
    return getWalletTransactions(wallet, network, cursor, fiat,
        KeyConnectApiFactory.getInstance().getTransactionsApi());
  }

  /**
   * Gets the wallet transactions
   *
   * @param wallet Blockchain wallet
   * @param network Blockchain network
   * @param cursor Optional cursor value, used for pagination
   * @param fiat Fiat to see the equivalent value
   * @param transactionsApi Transactions API object to use to connect to the server
   * @return BlockchainAccountTransactions Wallet transactions
   * @throws ApiException If there was an issue connecting to the server
   */
  public static BlockchainAccountTransactions getWalletTransactions(BlockchainWallet wallet,
      String network, @Nullable String cursor, @Nullable String fiat,
      TransactionsApi transactionsApi)
      throws ApiException {
    return transactionsApi
        .getAccountTransactions(wallet.getChainId().name().toLowerCase(Locale.ROOT),
            wallet.getAddress(), network, cursor, fiat);
  }
}
