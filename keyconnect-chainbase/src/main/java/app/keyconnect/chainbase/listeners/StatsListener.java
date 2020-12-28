package app.keyconnect.chainbase.listeners;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

public class StatsListener {

  private static final Logger logger = LoggerFactory.getLogger(StatsListener.class);
  private final ScheduledExecutorService loggingThread = Executors.newScheduledThreadPool(1);
  private final AtomicInteger totalBlocksProcessed = new AtomicInteger(0);
  private final AtomicInteger totalTransactionsProcessed = new AtomicInteger(0);
  private final AtomicInteger totalTransactionsSaved = new AtomicInteger(0);
  private final AtomicReference<Block> lastBlockProcessed = new AtomicReference<>();

  public StatsListener() {
    initLoggingThread();
  }

  private void initLoggingThread() {
    loggingThread.scheduleAtFixedRate(
        () -> logger.info(
            "Processed: lastBlockNumber={} totalBlocks={} totalTransactions={} savedTransactions={}",
            lastBlockProcessed.get().getNumber().toString(),
            totalBlocksProcessed.get(),
            totalTransactionsProcessed.get(),
            totalTransactionsSaved.get()
        ), 5L, 5L, TimeUnit.SECONDS);
  }

  public void newBlockProcessed(Block block) {
    lastBlockProcessed.set(block);
    totalBlocksProcessed.incrementAndGet();
  }

  public void newTransactionSaved() {
    totalTransactionsSaved.incrementAndGet();
  }

  public void transactionsProcessed(int count) {
    totalTransactionsProcessed.addAndGet(count);
  }

  public void stop() {
    loggingThread.shutdown();
  }
}
