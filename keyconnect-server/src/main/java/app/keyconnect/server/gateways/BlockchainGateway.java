package app.keyconnect.server.gateways;

import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountPayments;
import app.keyconnect.api.client.model.BlockchainAccountTransaction;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.BlockchainNetworkServerStatus;
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;

public interface BlockchainGateway {

  String getChainId();

  String[] getNetworks();

  BlockchainNetworkServerStatus[] getNetworkServerStatus(String network)
      throws UnknownNetworkException;

  BlockchainFee getFee(String network) throws UnknownNetworkException;

  BlockchainAccountInfo getAccount(String network, String accountId) throws UnknownNetworkException;

  BlockchainAccountTransactions getTransactions(String accountId, String network, int limit,
      String cursor)
      throws UnknownNetworkException;

  BlockchainAccountPayments getPayments(String accountId, String network, int limit,
      String cursor)
      throws UnknownNetworkException;

  BlockchainAccountTransaction getTransaction(String network, String hash)
      throws UnknownNetworkException;

  SubmitTransactionResult submitTransaction(String network, SubmitTransactionRequest submitTransactionRequest)
      throws UnknownNetworkException;
}
