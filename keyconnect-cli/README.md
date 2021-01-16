# Key Connect CLI

Command line interface for the Key Connect Server.

## Build
To compile the package the artifacts, run:
```
mvn package
```

## Usage
Run `java -jar keyconnect-cli-1.10-SNAPSHOT-jar-with-dependencies.jar -h` to view usage instructions:
```
Usage: kc [-hV] [COMMAND]
Key Connect command line interface
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  server-status, server, srv               Print server status
  status, s                                Print high-level status of supported
                                             block chains
  accounts, a                              Print information for a given
                                             account / wallet address
  account-transactions, transactions, txs  Print transactions for a given
                                             account / wallet address
  transaction, tx, txn, hash               Print details of a given transaction
                                             ID (hash)
  fees                                     Print fees for a given blockchain
Exit codes:
  10            Generic non-api related error, mostly an issue with the CLI or
                  the environment
  20            Network or connection related error
  [HTTP Code]   Exit code is the HTTP status code returned by the server
```
