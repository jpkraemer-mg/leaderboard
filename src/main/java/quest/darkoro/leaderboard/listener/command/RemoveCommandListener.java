package quest.darkoro.leaderboard.listener.command;

import static java.awt.Color.RED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@RequiredArgsConstructor
@Slf4j
@SecondaryListener
public class RemoveCommandListener extends ListenerAdapter {

  private final BoardService boardService;
  private final GuildService guildService;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("remove") || e.isAcknowledged()) {
      return;
    }
    if (e.getSubcommandName().equals("global")) {
      handleRemove(e, true);
      return;
    }
    if (e.getSubcommandName().equals("faction")) {
      handleRemove(e, false);
      return;
    }
    e.reply("Unknown subcommand '%s'".formatted(e.getSubcommandName())).setEphemeral(true).queue();
  }

  private void handleRemove(SlashCommandInteractionEvent e, boolean global) {
    var name = e.getOption("username").getAsString();
    var board = boardService.getEntryForRemoval(name, global);
    if (board.isEmpty()) {
      e.reply("No entry for the user '%s' could be found in the %s leaderboard.".formatted(name,
          global ? "global" : "faction")).setEphemeral(true).queue();
      return;
    }
    var entry = board.get();
    e.reply(prepareMessage(e.getJDA(), entry, global)).queue();
//    if (global) {
//      e.reply("Global not implemented").setEphemeral(true).queue();
//    } else {
//      e.reply("Faction not implemented").setEphemeral(true).queue();
//    }
  }

  private MessageEmbed prepareEmbed(Board b, boolean global) {
    return new EmbedBuilder()
        .setTitle((global ? "Global " : "") + "Leaderboard Removal Request")
        .setColor(RED)
        .addField("Username", b.getName(), true)
        .addField("Level", String.valueOf(b.getLevel()), true)
        .addField("Entry ID", b.getId().toString(), false)
        .build();
  }

  private MessageCreateData prepareMessage(JDA bot, Board b, boolean global) {
    return new MessageCreateBuilder()
        .addEmbeds(prepareEmbed(b, global))
        .addActionRow(
            Button.success("accept_remove", Emoji.fromUnicode("✅")),
            Button.danger("deny_remove", Emoji.fromUnicode("\uD83D\uDEAB"))
        )
        .addContent(String.format("<@&%s>",
            bot.getGuildById(b.getGuildId()).getRoleById(
                guildService.getGuildByGuildId(b.getGuildId()).get().getPermitted()).getId()
            )
        )
        .build();
  }
}
