package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.ServerStatusResponse;
import app.keyconnect.api.client.model.ServerStatusResponse.StatusEnum;
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
