package quest.darkoro.leaderboard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quest.darkoro.leaderboard.persistence.models.Guild;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {}
