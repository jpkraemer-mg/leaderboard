package quest.darkoro.leaderboard.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.PrimaryListener;
import quest.darkoro.leaderboard.services.RegisterService;

@Service
@Slf4j
@RequiredArgsConstructor
@PrimaryListener
public class GuildJoinListener extends ListenerAdapter {

  private final RegisterService registerService;

  @Override
  public void onGuildJoin(GuildJoinEvent e) {
    log.info("Joined new Guild {} ({}), registering commands", e.getGuild().getName(),
        e.getGuild().getId());
    registerService.registerCommands(e.getGuild());
  }
}
