package app.keyconnect.server.services.rate;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountTransaction;
import app.keyconnect.api.client.model.BlockchainAccountTransactionItem;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.CurrencyValue;
import app.keyconnect.api.client.model.GenericCurrencyValue;
import app.keyconnect.server.services.rate.models.Rate;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;

public class RateHelper {

  private final RateService rateService;

  public RateHelper(RateService rateService) {
    this.rateService = rateService;
  }

  public void applyFiatValueToTransactionItem(String fiat, BlockchainAccountTransactionItem transaction) {
    if (transaction != null
      && transaction.getAmount() != null
      && !StringUtils.isBlank(transaction.getAmount().getAmount())) {
      final CurrencyValue transactionAmount = transaction.getAmount();
      final Rate rate = rateService.getRate(transactionAmount.getCurrency().getValue(), fiat);
      if (rate != null) {
        transaction
            .value(
                new GenericCurrencyValue()
                    .amount(
                        rate.calculate(new BigDecimal(transactionAmount.getAmount()))
                            .toString()
                    )
                    .currency(fiat)
            );
      }
    }
  }

  public void applyFiatValueToTransaction(String fiat, BlockchainAccountTransaction transaction) {
    if (!StringUtils.isBlank(fiat)
        && transaction != null
        && transaction.getTransaction() != null) {
      applyFiatValueToTransactionItem(fiat, transaction.getTransaction());
    }
  }

  public void applyFiatValueToTransactions(String fiat,
      BlockchainAccountTransactions transactions) {
    if (null != transactions
        && transactions.getTransactions() != null) {
      transactions.getTransactions()
          .forEach(t -> applyFiatValueToTransactionItem(fiat, t));
    }
  }

  public void applyFiatToAccount(String fiat, BlockchainAccountInfo account) {
    if (!StringUtils.isBlank(fiat)
        && account != null
        && account.getBalance() != null
        && !StringUtils.isBlank(account.getBalance().getAmount())) {
      final CurrencyValue balance = account.getBalance();
      final Rate rate = rateService.getRate(balance.getCurrency().getValue(), fiat);
      if (rate != null) {
        account.value(
            new GenericCurrencyValue()
                .amount(
                    rate.calculate(
                        new BigDecimal(balance.getAmount()))
                        .toString()
                )
                .currency(fiat)
        );
      }
    }
  }
}
