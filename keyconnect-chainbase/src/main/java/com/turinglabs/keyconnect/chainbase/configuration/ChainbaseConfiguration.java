package com.turinglabs.keyconnect.chainbase.configuration;

import com.turinglabs.keyconnect.chainbase.indexers.EthBlockProcessor;
import com.turinglabs.keyconnect.chainbase.indexers.listeners.EthBlockListener;
import com.turinglabs.keyconnect.chainbase.listeners.StatsListener;
import com.turinglabs.keyconnect.chainbase.persistence.repositories.EthTransactionRepository;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ChainbaseConfiguration {

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Bean(destroyMethod = "stop")
  public EthBlockListener ethBlockListener(Environment env, EthBlockProcessor ethBlockProcessor) {
    final EthBlockListener ethBlockListener = new EthBlockListener(
        env.getProperty("ethnode.httpAddress", String.class), ethBlockProcessor, env.getProperty("last-block",
        BigInteger.class));
    executorService.submit(ethBlockListener); // todo make perpetual, this will quit if task dies 🤞
    return ethBlockListener;
  }

  @Bean(destroyMethod = "stop")
  public StatsListener statsListener() {
    return new StatsListener();
  }

  @Bean
  public EthBlockProcessor ethBlockProcessor(
      EthTransactionRepository ethTransactionRepository,
      StatsListener statsListener) {
    return new EthBlockProcessor(ethTransactionRepository, statsListener);
  }

}
