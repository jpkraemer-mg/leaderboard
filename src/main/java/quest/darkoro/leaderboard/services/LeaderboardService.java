package quest.darkoro.leaderboard.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class LeaderboardService {
  private final JDA bot;
  private final GuildService guildService;
  private final BoardService boardService;

  @Scheduled(fixedRate = 300000L)
  public void updateLeaderboards() {
    var guilds = guildService.getAllGuilds();
    bot.getGuilds().stream().filter(
        guild -> guilds.stream().anyMatch(
            configured -> configured.getGuildId().equals(guild.getIdLong())
        )
    ).forEach(
        guild -> {
          var check = guildService.getGuildByGuildId(guild.getIdLong()).get();
          int limit = 15;
          if (check.getTop() != null) {
            limit = check.getTop() > 0 ? check.getTop() : 15;
          }
          var entries = boardService.findTopEntriesByGuildId(guild.getIdLong(), limit);
          log.info("{} is configured has and {} out of {} entries",
              guild.getName(),
              entries.size(),
              limit
          );
        }
    );

  }
}
