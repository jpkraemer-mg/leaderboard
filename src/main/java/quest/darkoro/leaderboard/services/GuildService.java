package quest.darkoro.leaderboard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.GuildRepository;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Service
@RequiredArgsConstructor
public class GuildService {
    private final GuildRepository guildRepository;

    public Guild getGuildByGuildId(Long guildId) {
        return guildRepository.getGuildByGuildId(guildId);
    }
}
