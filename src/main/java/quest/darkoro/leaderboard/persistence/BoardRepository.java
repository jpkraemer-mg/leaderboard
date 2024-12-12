package quest.darkoro.leaderboard.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quest.darkoro.leaderboard.persistence.models.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  Optional<Board> getBoardByGuildId(Long guildId);

  Optional<Board> findBoardById(UUID id);
}
