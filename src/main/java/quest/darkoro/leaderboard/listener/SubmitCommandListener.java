package quest.darkoro.leaderboard.listener;

import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.interfaces.BaseListener;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmitCommandListener extends ListenerAdapter implements BaseListener {

  private final GuildService guildService;
  private final BoardService boardService;


  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("submit") || e.isAcknowledged()) {
      return;
    }
    var g = e.getGuild().getIdLong();
    if (guildService.getGuildByGuildId(g).isEmpty()) {
      e.reply("You must configure the bot first!\nUse `/configure` for this.")
          .setEphemeral(true)
          .queue();
      return;
    }
    var u = e.getOption(e.getUserLocale() != GERMAN ? "username" : "nutzername");
    Board b = boardService.saveBoard(
        new Board()
            .setGuildId(g)
            .setShared(e.getOption("shared") != null && (e.getOption("shared").getAsBoolean()))
            .setPending(true)
            .setLevel(e.getOption("level").getAsInt())
            .setName(u.getAsString())
    );
    e.reply(String.format("User %s\nLevel %s\nGuild %s\nPublic %s",
        b.getName(),
        b.getLevel(),
        b.getGuildId(),
        b.isShared() ? "Yes" : "No")
    ).queue();
  }
}
