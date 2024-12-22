package quest.darkoro.leaderboard.listener.button;

import static java.awt.Color.RED;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class DenyRemoveButtonListener extends ListenerAdapter {

  private final BoardService boardService;
  private final GuildService guildService;

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged() || !e.getButton().getId().equals("deny_remove")) {
      return;
    }
    var embed = e.getMessage().getEmbeds().get(0);
    var global = embed.getTitle().toLowerCase().contains("global");
    var permitted = e.getGuild()
        .getRoleById(guildService.getGuildByGuildId(e.getGuild().getIdLong()).get().getPermitted());
    if (!e.getGuild().retrieveMemberById(e.getUser().getId()).complete().getRoles()
        .contains(permitted)) {
      e.reply("You don't have permission to deny removal requests for the %s leaderboard!".formatted(
          global ? "global" : "faction"
      )).setEphemeral(true).queue();
      return;
    }
    var bid = UUID.fromString(embed.getFields().get(2).getValue());
    var board = boardService.getBoardById(bid);
    e.editMessageEmbeds(
            new EmbedBuilder()
                .setFooter(
                    String.format("Denied by %s - %s",
                        e.getUser().getEffectiveName(),
                        e.getGuild().getName())
                )
                .addField("Username", embed.getFields().get(0).getValue(), true)
                .addField("Level", embed.getFields().get(1).getValue(), true)
                .addField("Submission ID", board.get().getId().toString(), false)
                .setColor(RED)
                .build()
        )
        .setActionRow(e.getButton().asDisabled())
        .setContent("")
        .queue();
    boardService.deleteBoardByBoardId(bid);
  }
}
