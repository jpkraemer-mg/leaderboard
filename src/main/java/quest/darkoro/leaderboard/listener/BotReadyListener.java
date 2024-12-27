package quest.darkoro.leaderboard.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.PrimaryListener;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.annotations.TertiaryListener;
import quest.darkoro.leaderboard.services.RegisterService;

@RequiredArgsConstructor
@Service
@Slf4j
public class BotReadyListener extends ListenerAdapter {

  private final ApplicationContext applicationContext;
  private final RegisterService registerService;

  @Override
  public void onReady(ReadyEvent e) {
    var bot = e.getJDA();
    registerService.registerCommands(bot);
    var annotations = List.of(PrimaryListener.class, SecondaryListener.class,
        TertiaryListener.class);
    annotations.forEach(a -> {
      applicationContext.getBeansWithAnnotation(a)
          .values()
          .forEach(bot::addEventListener);
      log.info("Registered {} successfully: {}",
          a.getSimpleName(),
          applicationContext.getBeansWithAnnotation(a)
              .values()
              .stream()
              .map(l -> l.getClass().getSimpleName())
              .toList()
      );
    });
    log.info("Bot ready and running on {} Guild(s).", bot.getGuilds().size());
  }
}
