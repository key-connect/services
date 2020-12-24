# Key Connect API + Services

Key Connect API provides a single gateway to access multiple blockchains.

Services included:

* XRP Gateway
* ETH Gateway

## Developing with KeyConnect

Key Connect SDK is available across multiple programming languages. Here are a few examples:

### Java example

Import Key Connect API using standard dependency management.

```xml
<dependency>
  <groupId>com.turinglabs.keyconnect.api</groupId>
  <artifactId>keyconnect-api</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency> 
```

Up-to-date packages available on github [here](https://github.com/orgs/key-connect/packages?tab=packages&ecosystem=maven&q=%22com.turinglabs.keyconnect.api.keyconnect-api%22).

Start developing!

```java
import com.turinglabs.keyconnect.api.ApiClient;
import com.turinglabs.keyconnect.api.ApiException;
import com.turinglabs.keyconnect.api.client.DefaultApi;
import com.turinglabs.keyconnect.api.client.model.ServerStatusResponse;

class Scratch {

  public static void main(String[] args) {
    DefaultApi apiInstance = new DefaultApi(
        new ApiClient().setBasePath("https://www.keyconnect.app")
    );
    
    try {
      ServerStatusResponse result = apiInstance.getServerStatus();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getServerStatus");
      e.printStackTrace();
    }
  }
}
```

Running the above code should output something like:

```
class ServerStatusResponse {
    status: healthy
    errors: null
}
```

For more information about the API usage, visit the [API documentation](https://www.keyconnect.app/api-docs/index.html).

## Developing KeyConnect

### Pre-requisites

| Tool | Version |
| ---- | ------- |
| JDK | OpenJDK-15 |
| Maven | 3.6.3 |

*These are guide only, the application may still compile and run with higher versions.

### Build

The fastest way to get a build, without running tests is to run the following command:

```shell script
mvn clean install -DskipTests
```

If you want to run the built-in tests, which include unit and integration tests, you need to specify a few environment variables. 

We recommend building using the included run-configurations for Intellij. To use the provided run-configurations, copy the included `.idea-run-configurations` folder to `.run` and import the configurations into your Intellij IDE. Make sure to set appropriate values for the following variables:

| Variable | Value |
| --------- | ---- |
| ETHERSCAN_TOKEN | Token generated from [Etherscan API](https://etherscan.io/myapikey) |
| ETH_MAINNET_ADDR | Http(s) Address to ethereum node. You can use [infura.io](ETH_MAINNET_ADDR) if you don't have your own. |
| ETH_ROPSTEN_ADDR | Http(s) address to ethereum node. You can use [infura.io](ETH_MAINNET_ADDR) if you don't have your own. |

For full customisability you can edit `keyconnect-server/src/main/resources/application.yml` file for any application runtime configurations, and `keyconnect-server/src/test/resources/application.yml` for any test configurations.

You don't need to use Intellij to compile the project. The run configurations provided above are simply for convenience. You can compile the project manually as long as you have the above variables defined in the environment.

### Running the server

#### Development

Compile and package the source code by following the build instructions. To run the server in development mode, execute the following:

```shell script
cd keyconnect-server
mvn spring-boot:run
```

The server will listen on port `8080` by default. Hit ctrl+c to exit and stop the server at any time.

You will need to set the environment with the aforementioned environment variables in order to use Ethereum gateway. The XRP Gateway will work without any additional configuration.

#### Production

Similar to development, compile and package the source code by following the build instructions.

```shell script
java -DETH_MAINNET_ADDR=https://mainnet.infura.io/v3/INFURA_KEY -DETH_ROPSTEN_ADDR=https://ropsten.infura.io/v3/INFURA_KEY -DETHERSCAN_TOKEN=ETHERSCAN_TOKEN -jar keyconnect-server/target/keyconnect-server-1.0-SNAPSHOT.jar 
```

You can skip the `-D` jvm args if you don't need the ethereum gateway.

## Notes

### Ethereum integration

The aim of this project is to work directly with the blockchain nodes, without any third party APIs. In order to provide the functionality quicker and to test multi-blockchain integration, ethereum was integrated in two parts. 

1. All ethereum blockchain functionality available via Web3J APIs, except `getTransactions` (by account) and `getPayments` (by account)
2. Implement `getTransactions` (by account) and `getPayments` (by account) using [etherscan.io](https://etherscan.io/) APIs

The Etherscan APIs have a 5 calls/second limit. This rate-limiting is built into the `EtherscanUtil.java` - the implementation for the Etherscan APIs.

In order to be in line with the goals of this project, the Etherscan integration will have to be removed. To do this, the following needs to be true:

1. Indexed transactions data in a database
2. Add persistence to Key Connect server

\#1 is the in-progress `keyconnect-chainbase` module.
