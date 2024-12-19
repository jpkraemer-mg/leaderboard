package quest.darkoro.leaderboard.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quest.darkoro.leaderboard.persistence.models.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  Optional<Board> getBoardByGuildId(Long guildId);

  Optional<Board> findBoardById(UUID id);

  @Query("SELECT b FROM Board b WHERE b.guildId=:guildId AND b.name=:name AND b.shared=:shared AND b.pending=:pending")
  Optional<Board> getBoardByGuildIdAndNameAndSharedAndPending(Long guildId, String name,
      boolean shared, boolean pending);

  @Query(value = "SELECT * FROM board WHERE guild_id=:guildId AND shared=0 ORDER BY level DESC LIMIT :limit", nativeQuery = true)
  List<Board> findTopByGuildId(Long guildId, Integer limit);

  @Query(value = "SELECT * FROM board WHERE shared=1 ORDER BY level DESC LIMIT :limit", nativeQuery = true)
  List<Board> findTopAll(Integer limit);

  @Query(value = "SELECT * FROM board WHERE guild_id=:guildId AND shared=0 AND level=119989", nativeQuery = true)
  List<Board> findMaxLevelByGuildId(Long guildId);

  @Query(value = "SELECT * FROM board WHERE shared=1 AND level=119989", nativeQuery = true)
  List<Board> findMaxLevelAll();

  @Query(value = "SELECT * FROM board WHERE pending=0 AND processed=0 AND shared=0", nativeQuery = true)
  List<Board> findUnprocessed();

  @Query(value = "SELECT * FROM board WHERE pending=0 AND processed=0 AND shared=1", nativeQuery = true)
  List<Board> findSharedUnprocessed();

  @Query(value = "UPDATE board SET processed=1 WHERE pending=0 AND processed=0 AND shared=1", nativeQuery = true)
  @Modifying
  @Transactional
  void setSharedProcessed();

  @Query(value = "UPDATE board SET processed=1 WHERE guild_id=:guildId AND pending=0 AND processed=0", nativeQuery = true)
  @Modifying
  @Transactional
  void setProcessedByGuildId(Long guildId);

  @Query(value = "UPDATE board SET processed=1 WHERE pending=0 AND processed=0", nativeQuery = true)
  @Modifying
  @Transactional
  void setProcessed();

  @Query(value = "SELECT * FROM board WHERE processed=0 AND pending=0 AND shared=0 LIMIT 1", nativeQuery = true)
  Optional<Board> findDistinctAllByPendingAndProcessed();
}
