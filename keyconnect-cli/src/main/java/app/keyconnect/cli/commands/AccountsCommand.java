package app.keyconnect.cli.commands;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.cli.config.BaseAccountBlockchainConfig;
import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import app.keyconnect.cli.utils.LocalWalletData;
import app.keyconnect.cli.utils.LocalWalletHelper;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "accounts",
    aliases = {"a"},
    description = "Print information for a given account / wallet address"
)
public class AccountsCommand extends BaseAccountBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(getBlockchainApi()
        .getAccountInfo(chainId, getAccountAddress(), network, fiat));
    return 0;
  }
}
