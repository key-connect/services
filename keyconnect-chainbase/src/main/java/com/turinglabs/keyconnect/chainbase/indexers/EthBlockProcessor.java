package com.turinglabs.keyconnect.chainbase.indexers;

import com.turinglabs.keyconnect.chainbase.persistence.models.EthTransaction;
import com.turinglabs.keyconnect.chainbase.persistence.repositories.EthTransactionRepository;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.Transaction;

public class EthBlockProcessor implements Consumer<Block> {

  private static final Logger logger = LoggerFactory.getLogger(EthBlockProcessor.class);
  private final EthTransactionRepository ethTransactionRepository;

  public EthBlockProcessor(
      EthTransactionRepository ethTransactionRepository) {
    this.ethTransactionRepository = ethTransactionRepository;
  }

  @Override
  public void accept(Block block) {
    logger.info("Processing {} txns on block {}", block.getTransactions().size(), block.getNumber().toString());
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

          if(maybeSaved.isEmpty()) {
            ethTransactionRepository.save(ethTransaction);
//            logger.info("{} tx saved", transaction.getHash());
          }
        });
//    logger.info("Processed block {}", block.getNumber().toString());
  }
}
