package app.keyconnect.server.repository.entities;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestObject {

  @Id
  private String testObjectId;
  private String value;

  public TestObject() {
  }

  public TestObject(String value) {
    this.testObjectId = UUID.randomUUID().toString();
    this.value = value;
  }

  public String getTestObjectId() {
    return testObjectId;
  }

  public void setTestObjectId(String testObjectId) {
    this.testObjectId = testObjectId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
