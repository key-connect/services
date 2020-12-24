package com.turinglabs.keyconnect.server.controllers;

import com.turinglabs.keyconnect.api.client.model.ServerStatusResponse;
import com.turinglabs.keyconnect.api.client.model.ServerStatusResponse.StatusEnum;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerStatusController {

  @GetMapping(
      path = "/v1/server/status",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ServerStatusResponse> getServerStatus() {
    return ResponseEntity.ok(
        new ServerStatusResponse()
            .status(StatusEnum.HEALTHY)
    );
  }

}
