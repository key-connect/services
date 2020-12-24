package com.turinglabs.keyconnect.server.gateways;

import com.turinglabs.keyconnect.api.client.model.BlockchainAccountInfo;
import com.turinglabs.keyconnect.api.client.model.BlockchainAccountPayments;
import com.turinglabs.keyconnect.api.client.model.BlockchainAccountTransaction;
import com.turinglabs.keyconnect.api.client.model.BlockchainAccountTransactions;
import com.turinglabs.keyconnect.api.client.model.BlockchainFee;
import com.turinglabs.keyconnect.api.client.model.BlockchainNetworkServerStatus;
import com.turinglabs.keyconnect.api.client.model.CurrencyValue;
import com.turinglabs.keyconnect.api.client.model.SubmitTransactionRequest;
import com.turinglabs.keyconnect.api.client.model.SubmitTransactionResult;
import com.turinglabs.keyconnect.rippled.api.client.model.FeeResponse;
import com.turinglabs.keyconnect.server.gateways.exceptions.UnknownNetworkException;

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
