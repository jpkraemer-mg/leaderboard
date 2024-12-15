package quest.darkoro.leaderboard.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.interfaces.BaseListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnknownCommandListener extends ListenerAdapter implements BaseListener {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (e.isAcknowledged()) return;
    e.reply("Unknown command!").setEphemeral(true).queue();
  }
}
