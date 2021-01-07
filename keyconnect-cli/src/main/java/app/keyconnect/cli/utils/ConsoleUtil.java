package app.keyconnect.cli.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConsoleUtil {

  private static ConsoleMode mode = ConsoleMode.Serialize;
  private static final ObjectMapper jsonWriter = new ObjectMapper();
  private static final ObjectMapper yamlWriter = new ObjectMapper(new YAMLFactory());

  static {
    jsonWriter.setSerializationInclusion(Include.NON_NULL);
    yamlWriter.setSerializationInclusion(Include.NON_NULL);
  }

  public static void print(Object message) {
    final String output;
    switch (getMode()) {
      case Json:
        try {
          output = jsonWriter.writeValueAsString(message);
          break;
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Unable to output JSON format", e);
        }
      case Yaml:
        try {
          output = yamlWriter.writeValueAsString(message);
          break;
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Unable to output YAML format", e);
        }
      case Serialize:
      default:
        output = message.toString();
    }
    System.out.println(output);
  }

  public static void setMode(ConsoleMode mode) {
    ConsoleUtil.mode = mode;
  }

  public static ConsoleMode getMode() {
    return mode;
  }
}
