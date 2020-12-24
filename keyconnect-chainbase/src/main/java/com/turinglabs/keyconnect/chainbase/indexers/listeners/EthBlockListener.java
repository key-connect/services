package com.turinglabs.keyconnect.chainbase.indexers.listeners;

import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

public class EthBlockListener implements Runnable {

  private final Web3j client;
  private final Consumer<Block> blockConsumer;
  private BigInteger lastBlock;

  public EthBlockListener(final String httpAddress, Consumer<Block> blockConsumer, BigInteger lastBlock) {
    this.client = Web3j.build(new HttpService(httpAddress));
    this.blockConsumer = blockConsumer;
    this.lastBlock = lastBlock;
  }

  @SneakyThrows
  @Override
  public void run() {
    Disposable subscription = subscribe();
    while(!subscription.isDisposed()) {
      Thread.sleep(10000);
      if (subscription.isDisposed()) subscription = subscribe();
    }
  }

  @NotNull
  private Disposable subscribe() {
    return client.replayPastAndFutureBlocksFlowable(
        lastBlock == null ? DefaultBlockParameterName.EARLIEST : DefaultBlockParameter.valueOf(lastBlock), true
    ).forEach(ethBlock -> {
      final Block block = ethBlock.getBlock();
      blockConsumer.accept(block);
      lastBlock = block.getNumber();
    });
  }
}
