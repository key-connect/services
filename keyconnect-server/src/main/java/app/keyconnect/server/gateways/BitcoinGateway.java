package app.keyconnect.server.gateways;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountPayments;
import app.keyconnect.api.client.model.BlockchainAccountTransaction;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.BlockchainNetworkServerStatus;
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;

public class BitcoinGateway implements BlockchainGateway {

  public static final String CHAIN_ID = "btc";

  @Override
  public String getChainId() {
    return CHAIN_ID;
  }

  @Override
  public String[] getNetworks() {
    return new String[0];
  }

  @Override
  public String validateNetworkOrDefault(String network) throws UnknownNetworkException {
    return null;
  }

  @Override
  public BlockchainNetworkServerStatus[] getNetworkServerStatus(String network)
      throws UnknownNetworkException {
    return new BlockchainNetworkServerStatus[0];
  }

  @Override
  public BlockchainFee getFee(String network) throws UnknownNetworkException {
    return null;
  }

  @Override
  public BlockchainAccountInfo getAccount(String network, String accountId)
      throws UnknownNetworkException {
    /*try {
      final WalletAppKit kit = new WalletAppKit(NetworkParameters.fromID(NetworkParameters.ID_MAINNET), File.createTempFile("kc-server", System.currentTimeMillis() + "").getAbsoluteFile(), "btc-");

    } catch (IOException e) {
      e.printStackTrace();
    }*/

    return null;
  }

  @Override
  public void fundAccount(String network, String accountId) {

  }

  @Override
  public BlockchainAccountTransactions getTransactions(String accountId, String network, int limit,
      String cursor) throws UnknownNetworkException {
    return null;
  }

  @Override
  public BlockchainAccountPayments getPayments(String accountId, String network, int limit,
      String cursor) throws UnknownNetworkException {
    return null;
  }

  @Override
  public BlockchainAccountTransaction getTransaction(String network, String hash)
      throws UnknownNetworkException {
    return null;
  }

  @Override
  public SubmitTransactionResult submitTransaction(String network,
      SubmitTransactionRequest submitTransactionRequest) throws UnknownNetworkException {
    return null;
  }
}
