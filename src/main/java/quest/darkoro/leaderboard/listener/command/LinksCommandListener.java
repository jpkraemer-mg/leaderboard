package quest.darkoro.leaderboard.listener.command;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class LinksCommandListener extends ListenerAdapter {

  @Value("${quest.darkoro.omnitool.link}")
  private String omnitool;
  @Value("${quest.darkoro.trello.link}")
  private String trello;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("links") || e.isAcknowledged()) {
      return;
    }
    var link = e.getJDA().getInviteUrl(ADMINISTRATOR);
    e.reply("Invite the bot to your own server, check out the Omnitool or peruse the Trello!")
        .addActionRow(
            Button.link(link, "Invite"),
            Button.link(omnitool, "Omnitool"),
            Button.link(trello, "Trello"))
        .setEphemeral(true)
        .queue();
  }
}
