package app.keyconnect.sdk.api;

import app.keyconnect.api.client.AccountsApi;
import app.keyconnect.api.client.BatchApi;
import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.api.client.FundingApi;
import app.keyconnect.api.client.RatesApi;
import app.keyconnect.api.client.ServerApi;
import app.keyconnect.api.client.StatusApi;

public class KeyConnectApiFactory {

  private static KeyConnectApiFactory instance;

  private final BlockchainsApi defaultBlockchainsApi;
  private final AccountsApi defaultAccountsApi;
  private final FundingApi defaultFundingApi;
  private final BatchApi defaultBatchApi;
  private final RatesApi defaultRatesApi;
  private final ServerApi defaultServerApi;
  private final StatusApi defaultStatusApi;

  private KeyConnectApiFactory() {
    defaultBlockchainsApi = new BlockchainsApi();
    defaultAccountsApi = new AccountsApi();
    defaultFundingApi = new FundingApi();
    defaultBatchApi = new BatchApi();
    defaultRatesApi = new RatesApi();
    defaultServerApi = new ServerApi();
    defaultStatusApi = new StatusApi();
  }

  public static KeyConnectApiFactory getInstance() {
    if (null == instance) {
      instance = new KeyConnectApiFactory();
    }

    return instance;
  }

  public BlockchainsApi getDefaultBlockchainsApi() {
    return defaultBlockchainsApi;
  }

  public AccountsApi getDefaultAccountsApi() {
    return defaultAccountsApi;
  }

  public FundingApi getDefaultFundingApi() {
    return defaultFundingApi;
  }

  public BatchApi getDefaultBatchApi() {
    return defaultBatchApi;
  }

  public RatesApi getDefaultRatesApi() {
    return defaultRatesApi;
  }

  public ServerApi getDefaultServerApi() {
    return defaultServerApi;
  }

  public StatusApi getDefaultStatusApi() {
    return defaultStatusApi;
  }
}
