package app.keyconnect.server.services;

import app.keyconnect.server.services.networks.NetworkClientService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

public class EthCredentialsService implements CredentialsService<Credentials> {

  public static final String CREDENTIALS_DIRECTORY_RELATIVE =
      ".kcs" + File.separator + "credentials";
  public static final String PROPERTY_CREDENTIALS_PASSWORD = "kc.credentials.password";
  private static final Logger logger = LoggerFactory.getLogger(EthCredentialsService.class);
  private final Credentials credentials;

  public EthCredentialsService(Environment environment,
      NetworkClientService<Web3j> ethNetworkClientService) {
    String password = environment.getProperty(PROPERTY_CREDENTIALS_PASSWORD, String.class);
    if (null == password) {
      // we generate one
      final String generatedPassword = RandomStringUtils.randomAlphanumeric(32, 64);
      logger.warn(
          "{} property was not set, using a generated password for encrypting wallet, generatedPassword={}",
          PROPERTY_CREDENTIALS_PASSWORD, generatedPassword);
      password = generatedPassword;
    }

    final String userHomeAbsolutePath = SystemUtils.getUserHome().getAbsolutePath();
    final String kcPath = userHomeAbsolutePath + File.separator + CREDENTIALS_DIRECTORY_RELATIVE;
    final Path kcDirPath = Paths.get(kcPath);
    if (Files.notExists(kcDirPath)) {
      // create this path
      try {
        Files.createDirectories(kcDirPath);
      } catch (IOException e) {
        throw new IllegalStateException("Could not create directory path " + kcPath);
      }

      // create wallet credentials
      final String walletFile;
      try {
        walletFile = WalletUtils.generateNewWalletFile(password, kcDirPath.toFile());
        credentials = WalletUtils.loadCredentials(password, walletFile);
      } catch (CipherException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
        throw new IllegalStateException("Failed to generate wallet", e);
      }
    } else {
      // path exists
      // read wallet credentials
      try {
        credentials = WalletUtils.loadCredentials(password, kcDirPath.toFile());
      } catch (IOException | CipherException e) {
        throw new IllegalStateException("Failed to load wallet from " + kcDirPath.toAbsolutePath(),
            e);
      }
    }
  }

  @Override
  public Credentials getCredentials() {
    return credentials;
  }
}
