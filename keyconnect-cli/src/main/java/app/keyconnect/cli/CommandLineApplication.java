package app.keyconnect.cli;

import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.model.ExceptionalResponse;
import app.keyconnect.cli.commands.AccountTransactionsCommand;
import app.keyconnect.cli.commands.AccountsCommand;
import app.keyconnect.cli.commands.BlockchainStatusCommand;
import app.keyconnect.cli.commands.FeesCommand;
import app.keyconnect.cli.commands.ServerStatusCommand;
import app.keyconnect.cli.commands.TransactionCommand;
import com.google.gson.Gson;
import java.net.ConnectException;
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
        .setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
          if (!(ex instanceof ApiException)) {
            System.out.println(ex);
            return 10;
          }

          final ApiException apiException = (ApiException) ex;
          final Throwable exceptionCause = apiException.getCause();
          if (exceptionCause instanceof ConnectException) {
            System.out.println(exceptionCause.getMessage());
            return 20;
          }

          final String responseBody = apiException.getResponseBody();
          final ExceptionalResponse exceptionalResponse = new Gson()
              .fromJson(responseBody, ExceptionalResponse.class);
          System.out.println(exceptionalResponse);
          return 30;
        })
        .execute(args);
  }

}
