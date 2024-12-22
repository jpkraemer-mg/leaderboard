package quest.darkoro.leaderboard.configuration;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quest.darkoro.leaderboard.listener.BotReadyListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotConfiguration {

  @Value("${quest.darkoro.token}")
  private String token;
  private final BotReadyListener botReadyListener;

  @Bean
  public JDA bot() {
    JDA jda = JDABuilder
        .createLight(token, EnumSet.allOf(GatewayIntent.class))
        .addEventListeners(botReadyListener)
        .build();

    try {
      jda.awaitStatus(Status.INITIALIZING);
      log.info("INITIALIZING JDA");

      jda.awaitStatus(Status.INITIALIZED);
      log.info("INITIALIZED JDA");

      jda.awaitStatus(Status.LOGGING_IN);
      log.info("BOT LOGGING IN");

      jda.awaitStatus(Status.CONNECTING_TO_WEBSOCKET);
      log.info("Connecting to WebSocket");

      jda.awaitStatus(Status.IDENTIFYING_SESSION);
      log.info("IDENTIFYING SESSION");

      jda.awaitStatus(Status.AWAITING_LOGIN_CONFIRMATION);
      log.info("WAITING FOR LOGIN CONFIRMATION");

      jda.awaitStatus(Status.LOADING_SUBSYSTEMS);
      log.info("LOADING SUBSYSTEMS");

      jda.awaitStatus(Status.CONNECTED);
      log.info("CONNECTED");

      jda.awaitReady();
      log.info("BOT READY");
    } catch (InterruptedException e) {
      log.error("Error while initializing JDA", e);
      Thread.currentThread().interrupt();
    }
    return jda;
  }
}
