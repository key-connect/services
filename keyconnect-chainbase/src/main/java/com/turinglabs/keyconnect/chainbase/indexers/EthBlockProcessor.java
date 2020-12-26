package com.turinglabs.keyconnect.chainbase.indexers;

import com.turinglabs.keyconnect.chainbase.listeners.StatsListener;
import com.turinglabs.keyconnect.chainbase.persistence.models.EthTransaction;
import com.turinglabs.keyconnect.chainbase.persistence.repositories.EthTransactionRepository;
import java.util.Optional;
import java.util.function.Consumer;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.Transaction;

public class EthBlockProcessor implements Consumer<Block> {

  private final EthTransactionRepository ethTransactionRepository;
  private final StatsListener statsListener;

  public EthBlockProcessor(
      EthTransactionRepository ethTransactionRepository, StatsListener statsListener) {
    this.ethTransactionRepository = ethTransactionRepository;
    this.statsListener = statsListener;
  }

  @Override
  public void accept(Block block) {
    block.getTransactions()
        .forEach(tR -> {
          final TransactionObject tx = (TransactionObject) tR.get();
          final Transaction transaction = tx.get();
          final EthTransaction ethTransaction = EthTransaction.builder()
              .hash(transaction.getHash())
              .blockNumber(transaction.getBlockNumber().longValue())
              .from(transaction.getFrom())
              .to(transaction.getTo())
              .timestamp(block.getTimestamp().toString())
              .gasLimit(block.getGasLimit().toString())
              .gasPrice(transaction.getGasPrice().toString())
              .value(transaction.getValue().toString())
              .nonce(transaction.getNonce().toString())
              .input(transaction.getInput())
              .build();

          final Optional<EthTransaction> maybeSaved = ethTransactionRepository
              .findTopByHash(transaction.getHash());

          if (maybeSaved.isEmpty()) {
            statsListener.newTransactionSaved();
            ethTransactionRepository.save(ethTransaction);
          }
        });
    statsListener.transactionsProcessed(block.getTransactions().size());
    statsListener.newBlockProcessed(block);
  }
}
