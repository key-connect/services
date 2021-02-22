package app.keyconnect.sdk.wallets.io;

import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.sdk.wallets.DeterministicWallet;
import app.keyconnect.sdk.wallets.factories.BlockchainWalletFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class WalletWriter {

  private final DeterministicWallet wallet;

  public WalletWriter(DeterministicWallet wallet) {
    this.wallet = wallet;
  }

  public void writeToFile(File file, @Nullable String encryptionPassphrase) {
    final Optional<String> optionalEncryptionPassphrase = Optional.ofNullable(encryptionPassphrase);
    verifyFile(file);
    final WalletFile walletFile = new WalletFile(wallet.getMnemonicCode(), wallet.getPassphrase());

    wallet.getAllFactories()
        .stream()
        .filter(BlockchainWalletFactory::hasWallets)
        .forEachOrdered(f -> walletFile.getAccountIndices()
            .put(
                f.getChainIndex(),
                f.getGeneratedWallets().stream()
                    .map(BlockchainWallet::getName)
                    .collect(Collectors.joining(","))
            )
        );

    final String walletFileString = serializeWalletFileToString(walletFile);
    final byte[] data;
    data = optionalEncryptionPassphrase
        .map(s -> encrypt(walletFileString, s).getBytes(StandardCharsets.UTF_8))
        .orElseGet(() -> walletFileString.getBytes(StandardCharsets.UTF_8));

    try {
      final BufferedOutputStream outputStream = new BufferedOutputStream(
          new GZIPOutputStream(new FileOutputStream(file)));
      outputStream.write(data);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      throw new WalletWriterException("Unable to write to file", e);
    }
  }

  // https://stackoverflow.com/a/992413/929496
  private String encrypt(String walletFileString, String passphrase) {
    try {
      final byte[] saltBytes = new byte[8];
      new SecureRandom().nextBytes(saltBytes);
      final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      final KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), saltBytes, 65536, 256);
      final SecretKey tmp = factory.generateSecret(spec);
      final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

      final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secret);
      final AlgorithmParameters params = cipher.getParameters();
      byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
      byte[] ciphertext = cipher.doFinal(walletFileString.getBytes(StandardCharsets.UTF_8));
      return Base64.encodeBase64String(saltBytes)
          + ":" + Base64.encodeBase64String(iv)
          + ":" + Base64.encodeBase64String(ciphertext);
    } catch (NoSuchAlgorithmException
        | BadPaddingException
        | IllegalBlockSizeException
        | InvalidParameterSpecException
        | InvalidKeyException
        | NoSuchPaddingException
        | InvalidKeySpecException e) {
      throw new WalletWriterException("Problem encrypting wallet", e);
    }
  }

  private String serializeWalletFileToString(WalletFile walletFile) {
    try {
      return new ObjectMapper(new YAMLFactory()).writeValueAsString(walletFile);
    } catch (JsonProcessingException e) {
      throw new WalletWriterException("Failed to serialize wallet", e);
    }
  }

  private void verifyFile(File file) {
    if (file.isDirectory()) {
      file = new File(file.getAbsolutePath() + File.separator + ".wallet");
    }

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new WalletWriterException("Unable to create wallet file at " + file.getAbsolutePath(),
            e);
      }
    }
  }
}
