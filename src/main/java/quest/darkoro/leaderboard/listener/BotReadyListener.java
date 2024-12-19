package quest.darkoro.leaderboard.listener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.PrimaryListener;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.annotations.TertiaryListener;
import quest.darkoro.leaderboard.interfaces.BaseCommand;
import quest.darkoro.leaderboard.interfaces.BaseListener;
import quest.darkoro.leaderboard.persistence.models.Guild;
import quest.darkoro.leaderboard.services.GuildService;

@RequiredArgsConstructor
@Service
@Slf4j
public class BotReadyListener extends ListenerAdapter {

  private final List<BaseCommand> commands;
  private final ApplicationContext applicationContext;
  private final GuildService guildService;

  @Override
  public void onReady(ReadyEvent e) {
    var bot = e.getJDA();
    Objects.requireNonNull(
            bot
                .getGuildById("1114331993640017980")
        )
        .updateCommands()
        .addCommands(commands.stream().map(BaseCommand::create).toList())
        .queue(
            s -> log.info("Registered commands successfully: {}",
                commands.stream()
                    .map(c -> c.create().getName())
                    .toList()),
            error -> log.error("Failed to register commands", error)
        );
    var annotations = List.of(PrimaryListener.class, SecondaryListener.class, TertiaryListener.class);
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
