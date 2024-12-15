package quest.darkoro.leaderboard.controllers;

import jakarta.validation.constraints.Min;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import quest.darkoro.leaderboard.api.GuildsApi;
import quest.darkoro.leaderboard.api.model.Guild;
import quest.darkoro.leaderboard.api.model.GuildsQueryResult;
import quest.darkoro.leaderboard.persistence.GuildRepository;
import quest.darkoro.leaderboard.services.GuildService;
import quest.darkoro.leaderboard.utils.JpaApiMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuildController implements GuildsApi {

  private final GuildRepository guildRepository;
  private final GuildService guildService;

  @Override
  public ResponseEntity<GuildsQueryResult> apiGuildsGet(Optional<@Min(1) Integer> limit) {
    var guilds = guildRepository.findAll();
    var result = new GuildsQueryResult();
    result.setItems(guilds.stream().map(JpaApiMapper::toApi).toList());
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<Void> apiGuildsIdDelete(Long id) {
    return guildService.deleteGuildByGuildId(id);
  }

  @Override
  public ResponseEntity<Guild> apiGuildsPost(Guild guild) {
    var savedGuild = guildService.saveGuild(JpaApiMapper.toJpa(guild, null));
    return ResponseEntity.ok(JpaApiMapper.toApi(savedGuild));
  }
}
