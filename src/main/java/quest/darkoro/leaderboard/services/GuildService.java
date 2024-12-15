package quest.darkoro.leaderboard.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.GuildRepository;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Service
@RequiredArgsConstructor
public class GuildService {

  private final GuildRepository guildRepository;

  public Optional<Guild> getGuildByGuildId(Long guildId) {
    return guildRepository.getGuildByGuildId(guildId);
  }

  public Guild saveGuild(Guild guild) {
    return guildRepository.save(guild);
  }

  public List<Guild> getAllGuilds() {
    return guildRepository.findAll();
  }

  public ResponseEntity<Void> deleteGuildByGuildId(Long guildId) {
    return guildRepository.getGuildByGuildId(guildId)
        .map(guild -> {
          guildRepository.delete(guild);
          return ResponseEntity.noContent().<Void>build();
        })
        .orElse(ResponseEntity.accepted().build());
  }
}
