package app.keyconnect.server.services;

import org.web3j.crypto.Credentials;

public interface CredentialsService<T> {

  T getCredentials();
}
