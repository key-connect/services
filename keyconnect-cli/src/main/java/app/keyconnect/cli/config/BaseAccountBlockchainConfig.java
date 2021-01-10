package app.keyconnect.cli.config;

import picocli.CommandLine.Option;

public class BaseAccountBlockchainConfig extends BaseBlockchainConfig {

  @Option(
      names = {"-a", "--account"},
      description = "Account address",
      required = true
  )
  protected String accountAddress;

}
