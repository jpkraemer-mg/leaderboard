package quest.darkoro.leaderboard.listener.button;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class AcceptSubmitButtonListener extends ListenerAdapter {

  private final BoardService boardService;
  private final GuildService guildService;

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged() || !e.getButton().getId().equals("accept_submit")) {
      return;
    }
    var guildConfig = guildService.getGuildByGuildId(e.getGuild().getIdLong());
    if (guildConfig.isEmpty()) {
      e.reply("This server is no longer configured!\nUse `/configure server` first.")
          .setEphemeral(true).queue();
      return;
    }
    var permitted = e.getGuild().getRoleById(guildConfig.get().getPermitted());
    if (permitted == null) {
      e.reply("The configured moderator role no longer exists!\nUse `/configure server` to set a new one.")
          .setEphemeral(true).queue();
      return;
    }
    if (!e.getMember().getRoles().contains(permitted)) {
      e.reply("You don't have permission to accept this submission!").setEphemeral(true).queue();
      return;
    }
    var embed = e.getMessage().getEmbeds().get(0);
    var existing = boardService.getBoardByGuildIdAndNameAndShared(
        e.getGuild().getIdLong(),
        embed.getFields().get(0).getValue(),
        embed.getTitle().toLowerCase().contains("global")
    );
    Board b;
    if (existing.isPresent()) {
      b = boardService.saveBoard(
          existing.get()
              .setLevel(Integer.parseInt(embed.getFields().get(1).getValue()))
              .setProcessed(false)
      );
      if (!b.getId().toString().equals(embed.getFields().get(2).getValue())) {
        boardService.deleteBoardByBoardId(
            UUID.fromString(embed.getFields().get(2).getValue())
        );
      }
    } else {
      var pending = boardService.getBoardById(
          UUID.fromString(embed.getFields().get(2).getValue()));
      if (pending.isEmpty()) {
        e.reply("This submission no longer exists — it may have already been denied or removed.")
            .setEphemeral(true).queue();
        return;
      }
      b = boardService.saveBoard(pending.get().setPending(false));
    }
    boardService.mirrorGlobalToLocal(b);
    e.editMessageEmbeds(
            new EmbedBuilder()
                .setTitle(embed.getTitle())
                .setFooter(
                    String.format("Submission accepted by %s - %s",
                        e.getUser().getEffectiveName(),
                        e.getGuild().getName())
                )
                .addField("Username", embed.getFields().get(0).getValue(), true)
                .addField("Level", embed.getFields().get(1).getValue(), true)
                .addField("Submission ID", b.getId().toString(), false)
                .setColor(0x07C40D)
                .build()
        )
        .setActionRow(e.getButton().asDisabled())
        .setContent("")
        .queue();
  }
}
