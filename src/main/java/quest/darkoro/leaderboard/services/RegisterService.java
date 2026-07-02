package quest.darkoro.leaderboard.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterService {

  private final List<BaseCommand> commands;

  public void registerCommands(JDA bot) {
    bot.getGuilds().forEach(this::registerCommands);
  }

  public void registerCommands(Guild guild) {
    guild.updateCommands()
        .addCommands(commands.stream().map(BaseCommand::create).toList())
        .queue(
            s -> log.info("Registered commands for guild {} successfully: {}",
                guild.getName(),
                commands.stream()
                    .map(c -> c.create().getName())
                    .toList()),
            error -> log.error("Failed to register commands for guild {}", guild.getName(), error)
        );
  }
}
