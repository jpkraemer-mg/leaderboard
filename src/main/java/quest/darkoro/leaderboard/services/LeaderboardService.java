package quest.darkoro.leaderboard.services;

import static java.awt.Color.YELLOW;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.tuple.Pair;
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

  @Scheduled(fixedRate = 15000L)
  public void scanNew() {
    log.debug("Scanning...");
    var guilds = guildService.getAllGuilds();
    bot.getGuilds().stream().filter(
        guild -> guilds.stream().anyMatch(
            configured -> configured.getGuildId().equals(guild.getIdLong())
        )
    ).forEach(
        guild -> {
          var check = guildService.getGuildByGuildId(guild.getIdLong()).get();
          var pair = getEntriesAndMax(guild);
          var x = boardService.findUnprocessedByGuildId(guild.getIdLong());
          if (!x.isEmpty()) {
            updateLeaderboard(check, pair.getLeft(), guild, pair.getRight());
          }
        }
    );
  }

  @Scheduled(fixedRate = 1800000L)
  public void forceRenew() {
    log.info("Bi-Hourly leaderboard renewal");
    var guilds = guildService.getAllGuilds();
    bot.getGuilds().stream().filter(
        guild -> guilds.stream().anyMatch(
            configured -> configured.getGuildId().equals(guild.getIdLong())
        )
    ).forEach(
        guild -> {
          var pair = getEntriesAndMax(guild);
          var check = guildService.getGuildByGuildId(guild.getIdLong()).get();
          updateLeaderboard(check, pair.getLeft(), guild, pair.getRight());
        }
    );
  }

  private Pair<List<Board>, List<Board>> getEntriesAndMax(
      net.dv8tion.jda.api.entities.Guild guild) {
    var check = guildService.getGuildByGuildId(guild.getIdLong()).get();
    int limit =
        (check.getTop() == null || check.getTop() < 1) ? max : Math.min(check.getTop(), max);
    var entries = boardService.findTopEntriesByGuildId(guild.getIdLong(), limit);
    var entriesMax = boardService.findMaxLevelByGuildId(guild.getIdLong());
    return Pair.of(entries, entriesMax);
  }

  private void updateLeaderboard(Guild check, List<Board> entries,
      net.dv8tion.jda.api.entities.Guild guild, List<Board> entriesMax) {
    log.info("Updating leaderboard in Guild {} ({})", guild.getName(), guild.getId());
    var tchannel = bot.getTextChannelById(check.getChannelId());
    var nchannel = bot.getNewsChannelById(check.getChannelId());
    var channel = tchannel == null ? nchannel : tchannel;
    log.info("ID: {} | Channel: {}", check.getChannelId(), channel);
    var gid = check.getGlobal();
    var fid = check.getFaction();
    var globalUpdated = false;
    var updated = false;
    if (gid != null) {
      channel.retrieveMessageById(gid).queue(msg -> {
            if (msg.getAuthor().getId().equals(bot.getSelfUser().getId())) {
              var embed = createOrUpdate(entries, guild.getName(), entriesMax, true);
              msg.editMessageEmbeds(embed).queue();
            }
          },
          error -> {
            var embed = createOrUpdate(entries, guild.getName(), entriesMax, true);
            channel.sendMessageEmbeds(embed).queue(msg -> {
              guildService.saveGuild(check.setGlobal(msg.getIdLong()));
            });
          });
      globalUpdated = true;
    }
    if (fid != null) {
      channel.retrieveMessageById(fid).queue(msg -> {
            if (msg.getAuthor().getId().equals(bot.getSelfUser().getId())) {
              var embed = createOrUpdate(entries, guild.getName(), entriesMax, false);
              msg.editMessageEmbeds(embed).queue();
            }
          },
          error -> {
            var embed = createOrUpdate(entries, guild.getName(), entriesMax, false);
            channel.sendMessageEmbeds(embed).queue(msg -> {
              guildService.saveGuild(check.setFaction(msg.getIdLong()));
            });
          });
      updated = true;
    }

    if (!globalUpdated) {
      var globalEmbed = createOrUpdate(entries, guild.getName(), entriesMax, true);
      channel.sendMessageEmbeds(globalEmbed).queue(msg ->
          guildService.saveGuild(check.setGlobal(msg.getIdLong()))
      );
    }

    if (!updated) {
      var privateEmbed = createOrUpdate(entries, guild.getName(), entriesMax, false);
      channel.sendMessageEmbeds(privateEmbed).queue(msg ->
          guildService.saveGuild(check.setFaction(msg.getIdLong()))
      );
    }

    boardService.setProcessedByGuildId(check.getGuildId());
  }

  private MessageEmbed createOrUpdate(List<Board> entriesTop, String guildName,
      List<Board> entriesMax, boolean global) {
    if (global) {
      entriesTop = boardService.findTopAll(max);
      entriesMax = boardService.findMaxAll();
    }
    var embed = new EmbedBuilder()
        .setTitle(String.format((global ? "Global " : "") + "Leaderboard - Updated <t:%s:R>",
            Instant.now().getEpochSecond()))
        .setFooter(String.format("Leaderboard Bot - %s", guildName))
        .setColor(YELLOW);

    embed.addField(String.format("Max players (%d)", entriesMax.size()),
        generate(entriesMax, global),
        false);
    embed.addField(String.format("Top %s players", max), generate(entriesTop, global), false);
    return embed.build();
  }

  private String generate(List<Board> entries, boolean global) {
    int nameLength = entries.stream().mapToInt(e -> e.getName().length()).max().orElse(0);
    int guildLength = entries.stream().mapToInt(e -> bot.getGuildById(e.getGuildId()).getName().length()).max().orElse(0);
    int levelLength = entries.stream().mapToInt(e -> String.format("%,d", e.getLevel()).length())
        .max().orElse(0);
    StringBuilder sb = new StringBuilder();
    entries.forEach(e -> {
      var g = guildService.getGuildByGuildId(e.getGuildId()).get();
      sb.append(String.format(
          "`%-" + nameLength + "s` | `%," + levelLength + "d`" + (global ? " | `%-" + guildLength + "s`" : "") + "\n",
          e.getName(),
          e.getLevel(),
          g.getName()
      ));
    });
    return sb.toString();
  }
}
