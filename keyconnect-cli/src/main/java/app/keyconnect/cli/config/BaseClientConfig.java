package app.keyconnect.cli.config;

import app.keyconnect.api.ApiClient;
import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.api.client.RatesApi;
import app.keyconnect.api.client.ServerApi;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Option;

public class BaseClientConfig extends BaseConfig {

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

  private ApiClient apiClient;

  @NotNull
  protected BlockchainsApi getBlockchainApi() {
    return new BlockchainsApi(
        getApiClient()
    );
  }

  protected ServerApi getServerApi() {
    return new ServerApi(
        getApiClient()
    );
  }

  protected RatesApi getRatesApi() {
    return new RatesApi(
        getApiClient()
    );
  }

  private ApiClient getApiClient() {
    if (null == apiClient) {
      apiClient = new ApiClient()
          .setBasePath(baseUri)
          .setDebugging(apiDebug);
    }
    return apiClient;
  }
}
