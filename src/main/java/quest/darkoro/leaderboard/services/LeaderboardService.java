package quest.darkoro.leaderboard.services;

import static java.awt.Color.YELLOW;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

  private final JDA bot;
  private final GuildService guildService;
  private final BoardService boardService;

  @Value("${quest.darkoro.board.max}")
  private int max;

  @Scheduled(fixedRate = 15000L)
  public void scanNew() {
    log.debug("Scanning...");
    refreshLeaderboards(true);
  }

  @Scheduled(fixedRate = 1800000L)
  public void forceRenew() {
    log.info("Bi-Hourly leaderboard renewal");
    refreshLeaderboards(false);
  }

  private void refreshLeaderboards(boolean onlyUnprocessed) {
    if (onlyUnprocessed && !boardService.hasUnprocessedEntries()) {
      return;
    }
    // A new global entry changes the global board shown in every server,
    // so it forces a refresh of all configured guilds, not just its own
    var globalChanged = !onlyUnprocessed || boardService.hasUnprocessedSharedEntries();
    var configured = guildService.getAllGuilds().stream()
        .collect(Collectors.toMap(Guild::getGuildId, Function.identity()));
    // The global lists are identical for every guild, so fetch them once per run
    var globalTop = boardService.findTopAll(max);
    var globalMax = boardService.findMaxAll();
    bot.getGuilds().forEach(guild -> {
      var check = configured.get(guild.getIdLong());
      if (check == null) {
        return;
      }
      if (onlyUnprocessed && !globalChanged
          && boardService.findUnprocessedByGuildId(guild.getIdLong()).isEmpty()) {
        return;
      }
      int limit = getLimit(check);
      var entries = boardService.findTopEntriesByGuildId(guild.getIdLong(), limit);
      var entriesMax = boardService.findMaxLevelByGuildId(guild.getIdLong());
      updateLeaderboard(check, guild, entries, entriesMax,
          globalTop.subList(0, Math.min(limit, globalTop.size())), globalMax);
    });
    if (globalChanged) {
      // Shared rows from servers the bot has left would otherwise never be
      // marked processed and force a full refresh on every tick
      boardService.setProcessedShared();
    }
  }

  private int getLimit(Guild check) {
    return (check.getTop() == null || check.getTop() < 1) ? max : Math.min(check.getTop(), max);
  }

  private void updateLeaderboard(Guild check, net.dv8tion.jda.api.entities.Guild guild,
      List<Board> entries, List<Board> entriesMax, List<Board> globalTop, List<Board> globalMax) {
    log.info("Updating leaderboard in Guild {} ({})", guild.getName(), guild.getId());
    MessageChannel channel = bot.getTextChannelById(check.getChannelId());
    if (channel == null) {
      channel = bot.getNewsChannelById(check.getChannelId());
    }
    if (channel == null) {
      log.warn("Configured leaderboard channel {} not found in Guild {} ({})",
          check.getChannelId(), guild.getName(), guild.getId());
      return;
    }
    log.debug("ID: {} | Channel: {}", check.getChannelId(), channel);
    var globalEmbed = buildEmbed(globalTop, globalMax, guild.getName(), true, check);
    var factionEmbed = buildEmbed(entries, entriesMax, guild.getName(), false, check);
    renderBoard(channel, check.getGlobal(), globalEmbed,
        id -> guildService.saveGuild(check.setGlobal(id)));
    renderBoard(channel, check.getFaction(), factionEmbed,
        id -> guildService.saveGuild(check.setFaction(id)));
    boardService.setProcessedByGuildId(check.getGuildId());
  }

  private void renderBoard(MessageChannel channel, Long messageId, MessageEmbed embed,
      LongConsumer saveMessageId) {
    if (messageId == null) {
      channel.sendMessageEmbeds(embed).queue(msg -> saveMessageId.accept(msg.getIdLong()));
      return;
    }
    channel.retrieveMessageById(messageId).queue(
        msg -> {
          if (msg.getAuthor().getId().equals(bot.getSelfUser().getId())) {
            msg.editMessageEmbeds(embed).queue();
          }
        },
        error -> channel.sendMessageEmbeds(embed)
            .queue(msg -> saveMessageId.accept(msg.getIdLong()))
    );
  }

  private MessageEmbed buildEmbed(List<Board> entriesTop, List<Board> entriesMax, String guildName,
      boolean global, Guild check) {
    var limit = getLimit(check);
    var embed = new EmbedBuilder()
        .setTitle(String.format((global ? "Global " : "") + "Leaderboard - Updated <t:%s:R>",
            Instant.now().getEpochSecond()))
        .setFooter(String.format("Leaderboard Bot - %s", guildName))
        .setColor(YELLOW);
    addFields(embed, generate(entriesMax, global), "Max players (%d)".formatted(entriesMax.size()));
    addFields(embed, generate(entriesTop, global), "Top %d players".formatted(limit));
    return embed.build();
  }

  private String generate(List<Board> entries, boolean global) {
    Map<Long, String> guildNames = entries.stream()
        .map(Board::getGuildId)
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            id -> guildService.getGuildByGuildId(id).map(Guild::getName).orElse("Unknown")));
    int nameLength = entries.stream().mapToInt(e -> e.getName().length()).max().orElse(0);
    int guildLength = guildNames.values().stream().mapToInt(String::length).max().orElse(0);
    int levelLength = entries.stream().mapToInt(e -> String.format("%,d", e.getLevel()).length())
        .max().orElse(0);
    StringBuilder sb = new StringBuilder();
    entries.forEach(e -> sb.append(String.format(
        "`%-" + nameLength + "s` | `%," + levelLength + "d`" + (global ? " | `%-" + guildLength + "s`" : "") + "\n",
        e.getName(),
        e.getLevel(),
        guildNames.get(e.getGuildId())
    )));
    return sb.toString();
  }

  private void addFields(EmbedBuilder embed, String content, String title) {
    int length = 1024;

    String[] parts = content.split("\n");
    StringBuilder currentField = new StringBuilder();

    for (String part : parts) {
      if (currentField.length() + part.length() + 1 > length) {
        embed.addField(title, currentField.toString(), false);
        currentField = new StringBuilder();
      }
      if (!currentField.isEmpty()) {
        currentField.append("\n");
      }
      currentField.append(part);
    }

    if (!currentField.isEmpty()) {
      embed.addField(title, currentField.toString(), false);
    }
  }
}
