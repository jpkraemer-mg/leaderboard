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
public class AllCommandsListener extends ListenerAdapter implements BaseListener {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (e.getGuild() == null) {
      e.reply("You may only use commands from this bot in a Guild")
          .setEphemeral(true)
          .queue();
    }
  }
}
