package quest.darkoro.leaderboard.listener.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.PrimaryListener;

@Service
@Slf4j
@RequiredArgsConstructor
@PrimaryListener
public class AllCommandsListener extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (e.getGuild() == null) {
      e.reply("You may only use commands from this bot in a Guild")
          .setEphemeral(true)
          .queue();
    }
  }
}
