package quest.darkoro.leaderboard.listener.command;

import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;
import quest.darkoro.leaderboard.persistence.models.Guild;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class ConfigureCommandListener extends ListenerAdapter {

  private final GuildService guildService;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().matches("configure|konfiguration") || e.isAcknowledged()) {
      return;
    }
    if (e.getSubcommandName().equals("server")) {
      handleConfigureServer(e);
    }
    if (e.getSubcommandName().equals("profile")) {
      handleConfigureProfile(e);
    }
    e.reply(String.format("Unknown subcommand '%s'", e.getSubcommandName()));
  }

  private void handleConfigureServer(SlashCommandInteractionEvent e) {
    var l = e.getOption(e.getUserLocale() != GERMAN ? "leaderboards" : "bestenlisten")
        .getAsChannel();
    var s = e.getOption(e.getUserLocale() != GERMAN ? "submissions" : "einreichungen")
        .getAsChannel();
    var limit = e.getOption("limit");
    guildService.saveGuild(
        new Guild()
            .setGuildId(e.getGuild().getIdLong())
            .setName(e.getGuild().getName())
            .setChannelId(l.getIdLong())
            .setSubmissionChannelId(s.getIdLong())
            .setPermitted(e.getOption("permitted").getAsRole().getIdLong())
            .setTop(limit != null && limit.getAsInt() > 0 ? limit.getAsInt() : -1)
    );
    log.info("(Re)configured guild '{}' with board channel '{}' and submission channel '{}'",
        e.getGuild().getName(), l.getName(), s.getName());
    if (e.getUserLocale() == GERMAN) {
      e.reply("Konfiguration erfolgreich!").setEphemeral(true).queue();
      return;
    }
    e.reply("Configuration successful!").setEphemeral(true).queue();
  }

  private void handleConfigureProfile(SlashCommandInteractionEvent e) {
    e.getGuild().getSelfMember().modifyNickname(e.getOption("name").getAsString()).queue();
    e.reply("Successfully updated nickname!").setEphemeral(true).queue();
    log.info("Successfully updated nickname for guild '{}' ({}) to '{}'",
        e.getGuild().getId(),
        e.getGuild().getName(),
        e.getOption("name").getAsString());
  }
}
