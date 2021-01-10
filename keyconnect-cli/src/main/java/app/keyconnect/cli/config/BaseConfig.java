package app.keyconnect.cli.config;

import app.keyconnect.cli.utils.ConsoleMode;
import app.keyconnect.cli.utils.ConsoleUtil;
import picocli.CommandLine.Option;

public class BaseConfig {

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

}
