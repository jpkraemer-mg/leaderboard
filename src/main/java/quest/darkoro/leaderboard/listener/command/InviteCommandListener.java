package quest.darkoro.leaderboard.listener.command;

import static net.dv8tion.jda.api.Permission.MESSAGE_ATTACH_FILES;
import static net.dv8tion.jda.api.Permission.MESSAGE_SEND;
import static net.dv8tion.jda.api.Permission.NICKNAME_CHANGE;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class InviteCommandListener extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("invite") || e.isAcknowledged()) {
      return;
    }
    var link = e.getJDA().getInviteUrl(MESSAGE_ATTACH_FILES, MESSAGE_SEND, NICKNAME_CHANGE, VIEW_CHANNEL);
    e.reply("Invite the bot to your own server!")
        .addActionRow(Button.link(link, "Invite"))
        .setEphemeral(true)
        .queue();
  }
}
