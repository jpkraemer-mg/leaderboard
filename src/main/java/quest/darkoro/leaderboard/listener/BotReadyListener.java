package quest.darkoro.leaderboard.listener;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.commands.ConfigureCommand;
import quest.darkoro.leaderboard.commands.SubmitCommand;
import quest.darkoro.leaderboard.interfaces.BaseCommand;
import quest.darkoro.leaderboard.interfaces.BaseListener;

@RequiredArgsConstructor
@Service
@Slf4j
public class BotReadyListener extends ListenerAdapter {

  private final List<BaseCommand> commands;
  private final ApplicationContext applicationContext;

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
    applicationContext.getBeansOfType(BaseListener.class).values().forEach(bot::addEventListener);
    log.info("Registered listeners successfully: {}",
        applicationContext.getBeansOfType(BaseListener.class)
            .values()
            .stream()
            .map(l -> l.getClass().getSimpleName()).toList()
    );
    log.info("Bot ready!");
  }
}
