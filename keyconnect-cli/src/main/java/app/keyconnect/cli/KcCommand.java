package app.keyconnect.cli;

import picocli.CommandLine.Command;

@Command(name = "kc",
    description = "Key Connect command line interface",
    mixinStandardHelpOptions = true,
    exitCodeListHeading = "Exit codes:%n",
    exitCodeList = {
        "10:Generic non-api related error, mostly an issue with the CLI or the environment",
        "20:Network or connection related error",
        "[HTTP Code]:Exit code is the HTTP status code returned by the server"
    }
)
public class KcCommand {

}
