package app.keyconnect.cli.commands;

import app.keyconnect.cli.utils.LocalWalletData;
import app.keyconnect.cli.utils.LocalWalletHelper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "export",
    description = "Export the local wallet mnemonic and seed passphrase"
)
public class ExportWalletCommand implements Callable<Integer> {

  @Option(
      names = {"-f", "--file"},
      description = "Output to filepath",
      required = false
  )
  private String filename;

  @Override
  public Integer call() throws Exception {
    final Optional<File> exportFile;
    if (StringUtils.isNotBlank(filename)) {
      final File file = new File(filename);
      if (!file.createNewFile() && !file.canWrite()) {
        System.out.println("Cannot write to specified filepath " + filename);
        System.exit(1);
      }
      exportFile = Optional.of(file);
    } else {
      exportFile = Optional.empty();
    }

    final LocalWalletData localWalletData = LocalWalletHelper.readLocalWallet();
    final String mnemonicCode = localWalletData.getWallet().getMnemonic();
    final String passphrase = localWalletData.getWallet().getPassphrase();
    System.out.println();
    if (exportFile.isPresent()) {
      final File file = exportFile.get();
      final BufferedOutputStream outputStream = new BufferedOutputStream(
          new GZIPOutputStream(new FileOutputStream(file)));
      outputStream.write(mnemonicCode.getBytes(StandardCharsets.UTF_8));
      if (StringUtils.isNotBlank(passphrase)) {
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        outputStream.write(passphrase.getBytes(StandardCharsets.UTF_8));
      }
      outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      outputStream.close();
      System.out.println(
          "Wrote mnemonic and passphrase (if exists) to file " + filename + " in plain text.");
      System.out
          .println("Please make sure you delete the file after you have backed up its contents.");
    } else {
      System.out.println(mnemonicCode);
      if (StringUtils.isNotBlank(passphrase)) {
        System.out.println();
        System.out.println(passphrase);
      }
      System.out.println();
      System.out.println(
          "Please make sure you clear the terminal window after you have backed up the data above.");
    }
    return 0;
  }
}
