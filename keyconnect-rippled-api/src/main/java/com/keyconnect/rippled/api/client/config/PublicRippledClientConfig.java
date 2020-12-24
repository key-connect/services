package com.keyconnect.rippled.api.client.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PublicRippledClientConfig {

  private String jsonRpcEndpoint;

}
