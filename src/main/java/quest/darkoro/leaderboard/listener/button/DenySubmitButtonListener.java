package quest.darkoro.leaderboard.listener.button;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class DenySubmitButtonListener extends ListenerAdapter {

  private final BoardService boardService;
  private final GuildService guildService;

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged() || !e.getButton().getId().equals("deny_submit")) {
      return;
    }
    var permitted = e.getGuild()
        .getRoleById(guildService.getGuildByGuildId(e.getGuild().getIdLong()).get().getPermitted());
    if (!e.getGuild().retrieveMemberById(e.getUser().getId()).complete().getRoles()
        .contains(permitted)) {
      e.reply("You don't have permission to deny this submission!").setEphemeral(true).queue();
      return;
    }
    var embed = e.getMessage().getEmbeds().get(0);
    boardService.deleteBoardByBoardId(
        UUID.fromString(embed.getFields().get(2).getValue())
    );
    e.editMessageEmbeds(
            new EmbedBuilder(embed)
                .setFooter(
                    String.format("Submission denied by %s - %s",
                        e.getUser().getEffectiveName(),
                        e.getGuild().getName()
                    )
                )
                .setColor(0xFF2400)
                .build()
        )
        .setActionRow(e.getButton().asDisabled())
        .setContent("")
        .queue();
  }
}
