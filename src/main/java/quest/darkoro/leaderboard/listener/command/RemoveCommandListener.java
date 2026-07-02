package quest.darkoro.leaderboard.listener.command;

import static java.awt.Color.RED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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
    // Faction entries are per-server, so only match within the requesting guild;
    // global entries are unique by name across all guilds.
    var board = global
        ? boardService.getEntryForRemoval(name, true)
        : boardService.getLocalEntryForRemoval(e.getGuild().getIdLong(), name);
    if (board.isEmpty()) {
      e.reply("No entry for the user '%s' could be found in the %s leaderboard.".formatted(name,
          global ? "global" : "faction")).setEphemeral(true).queue();
      return;
    }
    var entry = board.get();
    var guildConfig = guildService.getGuildByGuildId(entry.getGuildId());
    if (guildConfig.isEmpty()) {
      e.reply("The server this entry belongs to is no longer configured, so the removal request can't be delivered.")
          .setEphemeral(true).queue();
      return;
    }
    var channel = e.getJDA().getTextChannelById(guildConfig.get().getSubmissionChannelId());
    if (channel == null) {
      e.reply("The submission channel of the server this entry belongs to no longer exists!\nA server administrator can set a new one with `/configure server`.")
          .setEphemeral(true).queue();
      return;
    }
    channel.sendMessage(prepareMessage(entry, global, guildConfig.get().getPermitted())).queue();
    e.reply("Your removal request was successfully sent!").setEphemeral(true).queue();
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

  private MessageCreateData prepareMessage(Board b, boolean global, Long permittedRoleId) {
    return new MessageCreateBuilder()
        .addEmbeds(prepareEmbed(b, global))
        .addActionRow(
            Button.success("accept_remove", Emoji.fromUnicode("✅")),
            Button.danger("deny_remove", Emoji.fromUnicode("\uD83D\uDEAB"))
        )
        .addContent(String.format("<@&%s>", permittedRoleId))
        .build();
  }
}
