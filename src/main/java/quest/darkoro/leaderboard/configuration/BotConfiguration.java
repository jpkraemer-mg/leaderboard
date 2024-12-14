package quest.darkoro.leaderboard.configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotConfiguration {

  @Value("${quest.darkoro.token}")
  private String token;

  @Bean
  public <T extends Event> GatewayDiscordClient a() {
    return DiscordClientBuilder.create(token).build().login().block();
  }
}
