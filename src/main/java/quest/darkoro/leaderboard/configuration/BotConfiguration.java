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
    log.debug("Preparing JDA");
    return JDABuilder
        .createLight(token, EnumSet.allOf(GatewayIntent.class))
        .addEventListeners(botReadyListener)
        .build();
  }
}
