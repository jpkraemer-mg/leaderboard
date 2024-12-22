package quest.darkoro.leaderboard.services;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class LeaderboardService {

  private final JDA bot;
  private final GuildService guildService;
  private final BoardService boardService;

  @Value("${quest.darkoro.board.max}")
  private int max;

  @Scheduled(fixedRate = 60000L)
  public void updateLeaderboards() {
    var guilds = guildService.getAllGuilds();
    bot.getGuilds().stream().filter(
        guild -> guilds.stream().anyMatch(
            configured -> configured.getGuildId().equals(guild.getIdLong())
        )
    ).forEach(
        guild -> {
          var check = guildService.getGuildByGuildId(guild.getIdLong()).get();
          int limit =
              (check.getTop() == null || check.getTop() < 1) ? max : Math.min(check.getTop(), max);
          var entries = boardService.findTopEntriesByGuildId(guild.getIdLong(), limit);
          log.info("{} is configured and has {} out of {} entries",
              guild.getName(),
              entries.size(),
              limit
          );
        }
    );
  }

  @Scheduled(fixedRate = 15000L)
  public void scanForUnprocessed() {
    log.info("Checking for unprocessed entries");
    var newEntries = boardService.findUnprocessed();
    List<Long> guilds = new ArrayList<>();
    for (Board entry : newEntries) {
      if (!guilds.contains(entry.getGuildId())) {
        guilds.add(entry.getGuildId());
      }
    }
    log.info("Found {} guild updates", guilds.size());
    if (!newEntries.isEmpty()) {
      bot.getTextChannelById(
              guildService.getGuildByGuildId(newEntries.get(0).getGuildId())
                  .get()
                  .getChannelId()
          )
          .sendMessage(MessageCreateData.fromEmbeds(
              prepareEmbed(
                  newEntries.get(0).isShared(),
                  guildService.getGuildByGuildId(newEntries.get(0).getGuildId()).get()
              ).build()))
          .queue();
      log.info("Found {} new entries", newEntries.size());
      boardService.setProcessed();
    }
  }

  public EmbedBuilder prepareEmbed(boolean shared, Guild guild) {
    return new EmbedBuilder()
        .setColor(Color.GREEN)
        .setTitle(String.format(
            "%sLeaderboard - Updated <t:%s:R>",
            shared ? "Global " : "",
            Instant.now().getEpochSecond()
        ))
        .setFooter(String.format("Leaderboard Bot - %s", guild.getName()));
  }

  public MessageEmbed finishEmbed(EmbedBuilder preparedEmbed) {
    return null;
  }
}
