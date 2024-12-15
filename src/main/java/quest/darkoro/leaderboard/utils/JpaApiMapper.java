package quest.darkoro.leaderboard.utils;

import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import quest.darkoro.leaderboard.api.model.Board;
import quest.darkoro.leaderboard.api.model.Guild;

public interface JpaApiMapper {

  static Guild toApi(quest.darkoro.leaderboard.persistence.models.Guild guild) {
    return new Guild()
        .guildId(guild.getGuildId())
        .channelId(guild.getChannelId())
        .name(guild.getName())
        .submissionChannelId(guild.getSubmissionChannelId())
        .permitted(guild.getPermitted());
  }

  static quest.darkoro.leaderboard.persistence.models.Guild toJpa(Guild guild, @Nullable
  quest.darkoro.leaderboard.persistence.models.Guild existingGuild) {
    return Optional.ofNullable(existingGuild).orElse(
            new quest.darkoro.leaderboard.persistence.models.Guild())
        .setGuildId(guild.getGuildId())
        .setChannelId(guild.getChannelId())
        .setName(guild.getName())
        .setSubmissionChannelId(guild.getSubmissionChannelId())
        .setPermitted(guild.getPermitted());
  }

  static Board toApi(quest.darkoro.leaderboard.persistence.models.Board board) {
    return new Board()
        .guildId(board.getGuildId())
        .id(board.getId())
        .level(board.getLevel())
        .shared(board.isShared())
        .pending(board.isPending())
        .name(board.getName());
  }

  static quest.darkoro.leaderboard.persistence.models.Board toJpa(Board board, @Nullable
  quest.darkoro.leaderboard.persistence.models.Board existingBoard) {
    return Optional.ofNullable(existingBoard).orElse(
            new quest.darkoro.leaderboard.persistence.models.Board())
        .setId(UUID.randomUUID())
        .setGuildId(board.getGuildId())
        .setLevel(board.getLevel())
        .setShared(board.getShared())
        .setPending(board.getPending())
        .setName(board.getName());
  }
}
