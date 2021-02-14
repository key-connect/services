package app.keyconnect.server;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class KeyConnectApplication {

  public static void main(String[] args) {
    SpringApplication.run(KeyConnectApplication.class, args);
  }

  @Bean
  public JettyServletWebServerFactory jettyServletWebServerFactory() {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.addServerCustomizers(server -> {
      GzipHandler gzipHandler = new GzipHandler();
      gzipHandler.setInflateBufferSize(1);
      gzipHandler.setHandler(server.getHandler());

      HandlerCollection handlerCollection = new HandlerCollection(gzipHandler);
      server.setHandler(handlerCollection);
    });
    return factory;
  }

}
