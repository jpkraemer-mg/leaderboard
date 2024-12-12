package quest.darkoro.leaderboard.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {
    Optional<Guild> getGuildByGuildId(Long guildId);
}
