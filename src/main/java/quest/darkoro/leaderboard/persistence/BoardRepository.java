package quest.darkoro.leaderboard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quest.darkoro.leaderboard.persistence.models.Board;
import quest.darkoro.leaderboard.persistence.models.Guild;

import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    Board getBoardByGuildId(Long guildId);
}
