package app.keyconnect.cli.config;

import app.keyconnect.api.ApiClient;
import app.keyconnect.api.client.DefaultApi;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Option;

public class BaseClientConfig {

  @Option(
      names = {"-s", "--server"},
      description = "Base URI of the server, if other than production https://api.keyconnect.app",
      defaultValue = "https://api.keyconnect.app"
  )
  protected String baseUri = "https://api.keyconnect.app";

  @NotNull
  protected DefaultApi buildApiClient() {
    return new DefaultApi(
        new ApiClient().setBasePath(baseUri)
    );
  }
}
