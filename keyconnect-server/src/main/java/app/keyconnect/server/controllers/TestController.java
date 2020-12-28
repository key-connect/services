package app.keyconnect.server.controllers;

import app.keyconnect.server.repository.entities.TestObject;
import app.keyconnect.server.repository.TestRepo;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "runtime-mode", havingValue = "test")
public class TestController {

  private final TestRepo testRepo;

  public TestController(TestRepo testRepo) {
    this.testRepo = testRepo;
  }

  @PostMapping(
      path = "/api/v1/test",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<TestObject> putTestObject(@RequestParam("value") String value) {
    final TestObject testObject = new TestObject(value);
    testRepo.save(testObject);
    return ResponseEntity.ok(testObject);
  }

  @GetMapping(
      path = "/api/v1/test",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<TestObject> getTestObject(@RequestParam("id") String id) {
    final Optional<TestObject> maybeObject = testRepo.findById(id);
    return ResponseEntity.of(maybeObject);
  }

}
