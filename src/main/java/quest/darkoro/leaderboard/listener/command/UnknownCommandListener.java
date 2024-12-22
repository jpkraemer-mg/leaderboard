package quest.darkoro.leaderboard.listener.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import quest.darkoro.leaderboard.annotations.TertiaryListener;

@Slf4j
@RequiredArgsConstructor
@TertiaryListener
public class UnknownCommandListener extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (e.isAcknowledged()) {
      return;
    }
    e.reply("Unknown command!").setEphemeral(true).queue();
  }
}
