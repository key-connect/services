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
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;

public class CommandLineApplication {

  public static final int GENERIC_ERROR_CODE = 10;
  public static final int CONNECTION_ERROR_CODE = 20;

  public static void main(String[] args) {
    AnsiConsole.systemInstall();
    final int exitCode = new CommandLine(new KcCommand())
        .addSubcommand(new ServerStatusCommand())
        .addSubcommand(new BlockchainStatusCommand())
        .addSubcommand(new AccountsCommand())
        .addSubcommand(new AccountTransactionsCommand())
        .addSubcommand(new TransactionCommand())
        .addSubcommand(new FeesCommand())
        .setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
          if (!(ex instanceof ApiException)) {
            System.out.println(ex);
            return GENERIC_ERROR_CODE;
          }

          final ApiException apiException = (ApiException) ex;
          final Throwable exceptionCause = apiException.getCause();
          if (exceptionCause instanceof ConnectException) {
            System.out.println(exceptionCause.getMessage());
            return CONNECTION_ERROR_CODE;
          }

          final String responseBody = apiException.getResponseBody();
          final ExceptionalResponse exceptionalResponse = new Gson()
              .fromJson(responseBody, ExceptionalResponse.class);

          System.out.println(exceptionalResponse);

          return apiException.getCode();
        })
        .setColorScheme(CommandLine.Help.defaultColorScheme(Ansi.ON))
        .execute(args);
    AnsiConsole.systemUninstall();
    System.exit(exitCode);
  }
}
