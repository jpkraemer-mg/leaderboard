package quest.darkoro.leaderboard.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterService {

  private final List<BaseCommand> commands;

  public void registerCommands(JDA bot) {
    bot.getGuilds().forEach(g ->
        g.updateCommands()
            .addCommands(commands.stream().map(BaseCommand::create).toList())
            .queue(
                s -> log.info("Registered commands for guild {} successfully: {}",
                    g.getName(),
                    commands.stream()
                        .map(c -> c.create().getName())
                        .toList()),
                error -> log.error("Failed to register commands", error)
            )
    );
  }
}
