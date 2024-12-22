package quest.darkoro.leaderboard.listener.command;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import quest.darkoro.leaderboard.annotations.SecondaryListener;

@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class InviteCommandListener extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("invite") || e.isAcknowledged()) {
      return;
    }
    var link = e.getJDA().getInviteUrl(ADMINISTRATOR);
    e.reply("Invite the bot to your own server!")
        .addActionRow(Button.link(link, "Invite"))
        .setEphemeral(true)
        .queue();
  }
}
