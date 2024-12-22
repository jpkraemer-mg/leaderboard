package quest.darkoro.leaderboard.listener.command;

import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.services.BoardService;
import quest.darkoro.leaderboard.services.GuildService;

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
    if (e.getSubcommandName().equals("global")) {
      try {
        handleSubmit(e, true);
      } catch (ExecutionException | InterruptedException ex) {
        log.error("Error submitting leaderboard submission, probably file", ex);
        e.reply("An error occurred while submitting your entry!").setEphemeral(true).queue();
      }
      return;
    }
    if (e.getSubcommandName().equals("faction")) {
      try {
        handleSubmit(e, false);
      } catch (ExecutionException | InterruptedException ex) {
        log.error("Error submitting leaderboard submission, probably file", ex);
        e.reply("An error occurred while submitting your entry!").setEphemeral(true).queue();
      }
      return;
    }
    e.reply("Unknown subcommand '%s'".formatted(e.getSubcommandName())).setEphemeral(true).queue();
  }

  private void handleSubmit(SlashCommandInteractionEvent e, boolean global)
      throws ExecutionException, InterruptedException {
    var bot = e.getJDA();
    var gid = e.getGuild().getIdLong();
    if (guildService.getGuildByGuildId(gid).isEmpty()) {
      e.reply(checkPermission(e.getMember())
              ? "You must configure the bot first!\nUse `/configure server` for this."
              : "A server administrator must configure the bot first!\nThey can use `/configure server` for this.")
          .setEphemeral(true)
          .queue();
      return;
    }
    Board b = boardService.saveBoard(
        new Board()
            .setGuildId(gid)
            .setShared(global)
            .setLevel(e.getOption("level").getAsInt())
            .setName(e.getOption("username").getAsString())
    );
    var guild = guildService.getGuildByGuildId(gid).get();
    var channel = bot.getTextChannelById(guild.getSubmissionChannelId());
    var embed = new EmbedBuilder()
        .setTitle((b.isShared() ? "Global " : "") + "Leaderboard Submission")
        .addField("Username", b.getName(), true)
        .addField("Level", String.valueOf(b.getLevel()), true)
        .addField("Submission ID", b.getId().toString(), false)
        .setColor(0x0000FF)
        .setFooter(String.format("Leaderboard Bot - %s", e.getGuild().getName()))
        .build();
    var proof = e.getOption("proof").getAsAttachment();
    channel.sendMessage(
        new MessageCreateBuilder()
            .addEmbeds(embed)
            .addContent(String.format("<@&%s>", guild.getPermitted()))
            .addFiles(FileUpload.fromData(proof.getProxy().download().get(), proof.getFileName()))
            .addActionRow(
                Button.success("accept_submit", Emoji.fromUnicode("✅")),
                Button.danger("deny_submit", Emoji.fromUnicode("\uD83D\uDEAB"))
            )
            .build()
    ).queue();
    e.reply("Your submission was sucessfully sent!").setEphemeral(true).queue();
  }

  private boolean checkPermission(Member m) {
    return m.hasPermission(Permission.ADMINISTRATOR) || m.hasPermission(Permission.MANAGE_SERVER);
  }

}