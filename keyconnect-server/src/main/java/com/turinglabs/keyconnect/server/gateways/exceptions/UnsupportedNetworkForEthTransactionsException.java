package com.turinglabs.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "We only support mainnet for viewing account transactions")
public class UnsupportedNetworkForEthTransactionsException extends
    RuntimeException {

}
