package app.keyconnect.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KeyConnectApplication {

  public static void main(String[] args) {
    SpringApplication.run(KeyConnectApplication.class, args);
  }

}
