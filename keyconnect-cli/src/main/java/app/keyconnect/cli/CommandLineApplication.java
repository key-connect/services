package app.keyconnect.cli;

import app.keyconnect.cli.commands.AccountTransactionsCommand;
import app.keyconnect.cli.commands.AccountsCommand;
import app.keyconnect.cli.commands.BlockchainStatusCommand;
import app.keyconnect.cli.commands.FeesCommand;
import app.keyconnect.cli.commands.ServerStatusCommand;
import app.keyconnect.cli.commands.TransactionCommand;
import picocli.CommandLine;

public class CommandLineApplication {

  public static void main(String[] args) {
    new CommandLine(new KcCommand())
        .addSubcommand(new ServerStatusCommand())
        .addSubcommand(new BlockchainStatusCommand())
        .addSubcommand(new AccountsCommand())
        .addSubcommand(new AccountTransactionsCommand())
        .addSubcommand(new TransactionCommand())
        .addSubcommand(new FeesCommand())
        .execute(args);
  }

}
