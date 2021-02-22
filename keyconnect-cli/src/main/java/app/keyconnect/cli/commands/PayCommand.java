package app.keyconnect.cli.commands;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import app.keyconnect.cli.utils.LocalWalletData;
import app.keyconnect.cli.utils.LocalWalletHelper;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.sdk.payments.Payment;
import app.keyconnect.sdk.payments.SubmittedPayment;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "pay",
    description = "Make a payment"
)
public class PayCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-f", "--from"},
      description = "Wallet name to make the payment from",
      required = true
  )
  private String from;

  @Option(
      names = {"-t", "--to"},
      description = "Wallet name to make the payment to (if --to-address is not specified)",
      required = false
  )
  private String to;

  @Option(
      names = {"-ta", "--to-address"},
      description = "Wallet address to make the payment to (if --to is not specified)",
      required = false
  )
  private String toAddress;

  @Option(
      names = {"-a", "--amount"},
      description = "Payment amount",
      required = true
  )
  private String amount;

  @Override
  public Integer call() throws Exception {
    final ChainIdEnum chain = ChainIdEnum.valueOf(this.chainId.toUpperCase(Locale.ROOT));

    if ((StringUtils.isNotBlank(to)
      && StringUtils.isNotBlank(toAddress))
      || (StringUtils.isBlank(to) && StringUtils.isBlank(toAddress))) {
      System.out.println("Please specify either one of --to or --to-address");
    }

    final LocalWalletData localWalletData = LocalWalletHelper
        .readLocalWallet();
    final Optional<BlockchainWallet> sourceWallet = localWalletData
        .getWallet()
        .getWalletFactory(chain)
        .getGeneratedWallets()
        .stream()
        .filter(w -> w.getName().equalsIgnoreCase(from))
        .findFirst();

    if (sourceWallet.isEmpty()) {
      System.out.println("Specified wallet to send the payment from could not be found.");
      System.exit(1);
    }

    final String destinationAddress;
    if (StringUtils.isNotBlank(toAddress)) {
      destinationAddress = toAddress;
    } else {
      final Optional<BlockchainWallet> foundDestinationWallet = localWalletData
          .getWallet()
          .getWalletFactory(chain)
          .getGeneratedWallets()
          .stream()
          .filter(w -> w.getName().equalsIgnoreCase(to))
          .findFirst();

      if (foundDestinationWallet.isEmpty()) {
        System.out.println("Specified destination wallet does not correspond to any known wallets.");
        System.exit(1);
      }

      destinationAddress = foundDestinationWallet.get().getAddress();
    }

    final SubmittedPayment payment = Payment.builder()
        .to(destinationAddress)
        .value(new BigDecimal(amount))
        .build()
        .sign(sourceWallet.get())
        .submit(getBlockchainApi(), network);

    if (null == payment.getTransaction() || StringUtils.isBlank(payment.getTransaction().getHash())) {
      System.out.println("Transaction could not be submitted.");
      System.exit(1);
    }

    ConsoleUtil.print(payment.getTransaction());

    System.out.println();
    System.out.println("Transaction submitted. Use hash " + payment.getTransaction().getHash() + " to check for status.");
    return 0;
  }
}
