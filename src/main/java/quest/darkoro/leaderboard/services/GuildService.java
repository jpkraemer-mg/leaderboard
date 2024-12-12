package quest.darkoro.leaderboard.services;

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

  public ResponseEntity<Void> deleteGuildByGuildId(Long guildId) {
    var guild = guildRepository.getGuildByGuildId(guildId);
    if (guild.isPresent()) {
      guild.ifPresent(guildRepository::delete);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }
}
