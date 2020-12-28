package app.keyconnect.server;

import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

public class TransactionObservableTest {

  private static final Logger logger = LoggerFactory.getLogger(TransactionObservableTest.class);
//  @Test
  public void tx() throws Exception {
    final Web3j client = Web3j.build(new HttpService("http://localhost:8545"));
    /*final Disposable flowable = client
        .replayPastBlocksFlowable(DefaultBlockParameterName.EARLIEST,DefaultBlockParameterName.LATEST, true)
        .forEach(b -> {
          logger.info(b.getBlock().getNumber().toString());
        });*/

    final Disposable flowable = client
        .replayPastAndFutureBlocksFlowable(DefaultBlockParameterName.EARLIEST, true)
        .forEach(b -> {
          System.out.println(b.getBlock().getNumber().toString());
        });
//    Thread.sleep(60 * 1000L);
  }
}
