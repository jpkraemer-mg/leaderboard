package quest.darkoro.leaderboard.listener.command;

import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class SubmitCommandListener extends ListenerAdapter {

  private final GuildService guildService;
  private final BoardService boardService;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().equals("submit") || e.isAcknowledged()) {
      return;
    }
    var bot = e.getJDA();
    var gid = e.getGuild().getIdLong();
    if (guildService.getGuildByGuildId(gid).isEmpty()) {
      e.reply("You must configure the bot first!\nUse `/configure` for this.")
          .setEphemeral(true)
          .queue();
      return;
    }
    var username = e.getOption(e.getUserLocale() != GERMAN ? "username" : "nutzername");
    Board b = boardService.saveBoard(
        new Board()
            .setGuildId(gid)
            .setShared(e.getOption("shared") != null && (e.getOption("shared").getAsBoolean()))
            .setPending(true)
            .setLevel(e.getOption("level").getAsInt())
            .setName(username.getAsString())
    );
    var guild = guildService.getGuildByGuildId(gid).get();
    var channel = bot.getTextChannelById(guild.getSubmissionChannelId());
    var embed = new EmbedBuilder()
        .setTitle(b.isShared() ? "Public Leaderboard Submission" : "Leaderboard Submission")
        .addField("Username", b.getName(), true)
        .addField("Level", String.valueOf(b.getLevel()), true)
        .addField("Submission ID", b.getId().toString(), false)
        .setColor(0x0000FF)
        .setFooter(String.format("Leaderboard Bot - %s", e.getGuild().getName()))
        .build();
    channel.sendMessage(
        new MessageCreateBuilder()
            .addEmbeds(embed)
            .addContent(String.format("<@&%s>", guild.getPermitted()))
            .addActionRow(
                Button.success("accept_submit", Emoji.fromUnicode("✅")),
                Button.danger("deny_submit", Emoji.fromUnicode("\uD83D\uDEAB"))
            )
            .build()
    ).queue();
    e.reply("Your submission was sucessfully sent!").setEphemeral(true).queue();
  }
}
