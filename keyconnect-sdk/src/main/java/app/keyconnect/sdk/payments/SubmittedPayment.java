package app.keyconnect.sdk.payments;

import app.keyconnect.api.client.model.BlockchainAccountTransactionItem;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubmittedPayment {

  private BlockchainAccountTransactionItem transaction;

}
