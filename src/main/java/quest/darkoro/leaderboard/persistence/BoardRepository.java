package quest.darkoro.leaderboard.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quest.darkoro.leaderboard.persistence.models.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  Optional<Board> getBoardByGuildId(Long guildId);

  Optional<Board> findBoardById(UUID id);

  @Query("SELECT b FROM Board b WHERE b.guildId=:guildId AND b.name=:name AND b.shared=:shared AND b.pending=:pending")
  Optional<Board> getBoardByGuildIdAndNameAndSharedAndPending(Long guildId, String name,
      boolean shared, boolean pending);

  List<Board> findTopByGuildId(Long guildId, Limit limit);
}
