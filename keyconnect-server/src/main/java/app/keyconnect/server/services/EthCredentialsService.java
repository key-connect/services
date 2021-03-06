package app.keyconnect.server.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

public class EthCredentialsService implements CredentialsService<Credentials> {

  public static final String CREDENTIALS_DIRECTORY_RELATIVE =
      ".kcs" + File.separator + "credentials";
  public static final String PROPERTY_CREDENTIALS_PASSWORD = "kc.credentials.password";
  public static final String PROPERTY_CREDENTIALS_HOME = "kc.credentials.home";
  public static final String PROPERTY_CREDENTIALS_FILE = "kc.credentials.file";
  private static final Logger logger = LoggerFactory.getLogger(EthCredentialsService.class);
  private final Credentials credentials;
  private String password;

  public EthCredentialsService(Environment environment) {
    password = environment.getProperty(PROPERTY_CREDENTIALS_PASSWORD, String.class);
    if (null == password) {
      // we generate one
      final String generatedPassword = RandomStringUtils.randomAlphanumeric(32, 64);
      logger.warn(
          "{} property was not set, using a generated password for encrypting wallet, generatedPassword={}",
          PROPERTY_CREDENTIALS_PASSWORD, generatedPassword);
      password = generatedPassword;
    }

    final String userHomeAbsolutePath = environment.getProperty(
        PROPERTY_CREDENTIALS_HOME,
        String.class,
        SystemUtils.getUserHome().getAbsolutePath()
    );
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
        final String walletFilePath = kcDirPath + File.separator + walletFile;
        credentials = WalletUtils
            .loadCredentials(password, walletFilePath);
        logger.info("Generated encrypted wallet {}", walletFilePath);
      } catch (CipherException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
          | NoSuchProviderException | IOException e) {
        throw new IllegalStateException("Failed to generate wallet", e);
      }
    } else {
      // path exists
      // read wallet credentials
      try {
        String walletFile = environment.getProperty(PROPERTY_CREDENTIALS_FILE, String.class);
        if (null == walletFile) {
          // find wallet
          final File[] foundWallets = Objects.requireNonNull(kcDirPath.toFile().listFiles(), "Listing wallet directory must return at least one wallet");
          StringBuilder walletString = new StringBuilder("Found wallets in " + kcDirPath.toAbsolutePath());
          walletString.append(System.lineSeparator());
          for (File foundWallet : foundWallets) {
            walletString.append("- ").append(foundWallet.getName());
          }
        logger.info(walletString.toString());
          walletFile = foundWallets[0].getName();
        }
        logger.info("Using wallet {}", walletFile);
        credentials = WalletUtils.loadCredentials(password, kcDirPath.toFile() + File.separator + walletFile);
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

  // for testing
  String getPassword() {
    return this.password;
  }
}
