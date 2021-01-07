package app.keyconnect.cli.config;

import app.keyconnect.api.ApiClient;
import app.keyconnect.api.client.DefaultApi;
import app.keyconnect.cli.utils.ConsoleMode;
import app.keyconnect.cli.utils.ConsoleUtil;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Option;

public class BaseClientConfig {

  @Option(
      names = {"-s", "--server"},
      description = "Base URI of the server, if other than production https://api.keyconnect.app",
      defaultValue = "https://api.keyconnect.app"
  )
  protected String baseUri = "https://api.keyconnect.app";

  @Option(
      names = {"--api-debug"},
      description = "Debug API request/responses",
      defaultValue = "false"
  )
  private boolean apiDebug;

  @Option(
      names = {"--print-json", "--json"},
      description = "Print output in JSON format",
      defaultValue = "false"
  )
  private void setJson(boolean value) {
    if (value)
      ConsoleUtil.setMode(ConsoleMode.Json);
  }

  @Option(
      names = {"--print-yaml", "--yaml"},
      description = "Print output in YAML format",
      defaultValue = "false"
  )
  private void setYaml(boolean value) {
    if (value)
      ConsoleUtil.setMode(ConsoleMode.Yaml);
  }

  @NotNull
  protected DefaultApi buildApiClient() {
    return new DefaultApi(
        new ApiClient()
            .setBasePath(baseUri)
            .setDebugging(apiDebug)
    );
  }
}
