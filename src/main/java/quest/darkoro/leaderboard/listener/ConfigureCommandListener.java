package quest.darkoro.leaderboard.listener;

import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.interfaces.BaseListener;
import quest.darkoro.leaderboard.persistence.models.Guild;
import quest.darkoro.leaderboard.services.GuildService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigureCommandListener extends ListenerAdapter implements BaseListener {

  private final GuildService guildService;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (!e.getName().matches("configure|konfiguration") || e.isAcknowledged()) {
      return;
    }
    var l = e.getOption(e.getUserLocale() != GERMAN ? "leaderboards" : "bestenlisten")
        .getAsChannel();
    var s = e.getOption(e.getUserLocale() != GERMAN ? "submissions" : "einreichungen")
        .getAsChannel();
    guildService.saveGuild(
        new Guild()
            .setGuildId(e.getGuild().getIdLong())
            .setName(e.getGuild().getName())
            .setChannelId(l.getIdLong())
            .setSubmissionChannelId(s.getIdLong())
    );
    log.info("(Re)configured guild '{}' with board channel '{}' and submission channel '{}'",
        e.getGuild().getName(), l.getName(), s.getName());
    if (e.getUserLocale() == GERMAN) {
      e.reply("Konfiguration erfolgreich!").setEphemeral(true).queue();
      return;
    }
    e.reply("Configuration successful!").setEphemeral(true).queue();

  }
}
