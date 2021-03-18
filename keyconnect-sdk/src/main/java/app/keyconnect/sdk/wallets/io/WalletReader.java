package app.keyconnect.sdk.wallets.io;

import app.keyconnect.sdk.wallets.DeterministicWallet;
import app.keyconnect.sdk.wallets.factories.BlockchainWalletFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
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
import org.apache.commons.lang3.StringUtils;

public class WalletReader {

  public static DeterministicWallet fromFile(File file, String password) {
    if (!file.exists() || !file.canRead()) {
      throw new WalletReaderException(
          "Cannot read wallet file, it may not exist or permissions may prevent read");
    }
    try {
      final String walletString;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(
          new GZIPInputStream(new FileInputStream(file))))) {
        walletString = reader.lines()
            .collect(Collectors.joining(System.lineSeparator()));
      }

      final String decryptedWalletString = decrypt(walletString, password);

      final WalletFile walletFile = new ObjectMapper(new YAMLFactory())
          .readValue(decryptedWalletString, WalletFile.class);
      final DeterministicWallet wallet = new DeterministicWallet(walletFile.getPassphrase(),
          walletFile.getMnemonic(),
          System.currentTimeMillis());
      // inflate trees
      walletFile.getAccountIndices()
          .keySet()
          .stream()
          .forEach(k -> {
            final String walletNamesValue = walletFile.getAccountIndices().get(k);
            final String[] walletNames = StringUtils.split(walletNamesValue, ",");

            final BlockchainWalletFactory walletFactory = wallet
                .getWalletFactory(k);

            for (String walletName : walletNames) {
              walletFactory.generateNext(walletName);
            }
          });
      return wallet;
    } catch (IOException e) {
      throw new WalletReaderException(
          "Unable to read / parse wallet, file may be malformed or tampered with", e);
    }
  }

  private static String decrypt(String walletString, String password) {
    if (!walletString.contains(":")) {
      throw new WalletReaderException("Encrypted wallet is malformed or has been tampered with");
    }
    final String[] encoded = walletString.split(":");
    if (encoded.length != 3) {
      throw new WalletReaderException(
          "Encrypted wallet missing parameters required for decryption");
    }

    final String saltString = encoded[0];
    final String ivString = encoded[1];
    final String cipherTextString = encoded[2];

    try {
      final byte[] salt = Base64.decodeBase64(saltString);
      final byte[] iv = Base64.decodeBase64(ivString);
      final byte[] ciphertext = Base64.decodeBase64(cipherTextString);

      final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
      final SecretKey tmp = factory.generateSecret(spec);
      final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

      final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
      return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException
        | InvalidKeySpecException
        | NoSuchPaddingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new WalletReaderException("Error decrypting wallet", e);
    }
  }
}
