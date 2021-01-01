package app.keyconnect.cli.config;

import picocli.CommandLine.Option;

public class BaseBlockchainConfig extends BaseClientConfig {

  @Option(
      names = {"-c", "--chainid"},
      description = "ID of the blockchain",
      required = true
  )
  protected String chainId;

  @Option(
      names = {"-n", "--network"},
      description = "Blockchain network, if other than server-side default"
  )
  protected String network;

}
