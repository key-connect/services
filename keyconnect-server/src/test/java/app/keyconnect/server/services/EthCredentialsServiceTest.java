package app.keyconnect.server.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

class EthCredentialsServiceTest {

  @Test
  void getCredentialsNew() throws Exception {
    final MockEnvironment environment = new MockEnvironment();
    final Path tempFilePath = Files.createTempDirectory(System.currentTimeMillis() + "");
    environment.setProperty(EthCredentialsService.PROPERTY_CREDENTIALS_HOME, tempFilePath.toAbsolutePath().toString());
    final CredentialsService<Credentials> subject = new EthCredentialsService(
        environment
    );

    final Credentials credentials = subject.getCredentials();
    assertThat(credentials).isNotNull();
  }

  @Test
  void getCredentialsNewAndLoad() throws Exception {
    final MockEnvironment environment = new MockEnvironment();
    final Path tempFilePath = Files.createTempDirectory(System.currentTimeMillis() + "");
    environment.setProperty(EthCredentialsService.PROPERTY_CREDENTIALS_HOME, tempFilePath.toAbsolutePath().toString());
    EthCredentialsService subject = new EthCredentialsService(
        environment
    );

    final Credentials credentials = subject.getCredentials();
    assertThat(credentials).isNotNull();

    environment.setProperty(EthCredentialsService.PROPERTY_CREDENTIALS_PASSWORD, subject.getPassword());
    subject = new EthCredentialsService(
        environment
    );

    final Credentials credentials2 = subject.getCredentials();
    assertThat(credentials2).isNotNull();
  }
}
