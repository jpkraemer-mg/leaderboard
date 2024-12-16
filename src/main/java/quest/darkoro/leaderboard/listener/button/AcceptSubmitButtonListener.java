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

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class AcceptSubmitButtonListener extends ListenerAdapter {

  private final BoardService boardService;

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged() || !e.getButton().getId().equals("accept_submit")) {
      return;
    }
    var embed = e.getMessage().getEmbeds().get(0);
    var existing = boardService.getBoardByGuildIdAndNameAndSharedAndPending(
        e.getGuild().getIdLong(),
        embed.getFields().get(0).getValue(),
        embed.getTitle().toLowerCase().contains("public"),
        false
    );
    Board b;
    if (existing.isPresent()) {
      b = boardService.saveBoard(
          existing.get()
              .setLevel(Integer.parseInt(embed.getFields().get(1).getValue()))
      );
      boardService.deleteBoardByBoardId(
          UUID.fromString(embed.getFields().get(2).getValue())
      );
    } else {
      b = boardService.saveBoard(
          boardService.getBoardById(
              UUID.fromString(embed.getFields().get(2).getValue())
              )
          .get()
          .setPending(false)
      );
    }
    e.editMessageEmbeds(
            new EmbedBuilder()
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
