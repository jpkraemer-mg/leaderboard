package quest.darkoro.leaderboard.utils;

import jakarta.annotation.Nullable;
import java.util.Optional;
import quest.darkoro.leaderboard.api.model.Guild;

public interface JpaApiMapper {
  static Guild toApi(quest.darkoro.leaderboard.persistence.models.Guild guild) {
    return new Guild()
        .guildId(guild.getGuildId())
        .channelId(guild.getChannelId())
        .name(guild.getName());
  }

  static quest.darkoro.leaderboard.persistence.models.Guild toJpa(Guild guild, @Nullable
  quest.darkoro.leaderboard.persistence.models.Guild existingGuild) {
    return Optional.ofNullable(existingGuild).orElse(
            new quest.darkoro.leaderboard.persistence.models.Guild())
        .setGuildId(guild.getGuildId())
        .setChannelId(guild.getChannelId())
        .setName(guild.getName());
  }
}
