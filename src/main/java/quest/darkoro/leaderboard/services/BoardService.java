package quest.darkoro.leaderboard.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.BoardRepository;
import quest.darkoro.leaderboard.persistence.models.Board;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final GuildService guildService;

  public Board saveBoard(Board board) {
    return boardRepository.save(board);
  }

  public ResponseEntity<Void> deleteBoardByBoardId(UUID boardId) {
    return boardRepository.findBoardById(boardId)
        .map(board -> {
          boardRepository.delete(board);
          return ResponseEntity.noContent().<Void>build();
        })
        .orElse(ResponseEntity.accepted().build());
  }

  public Optional<Board> getBoardById(UUID boardId) {
    return boardRepository.findBoardById(boardId);
  }

  public Optional<Board> getBoardByGuildIdAndNameAndSharedAndPending(Long guildId, String name,
      boolean shared, boolean pending) {
    return boardRepository.getBoardByGuildIdAndNameAndSharedAndPending(guildId, name, shared,
        pending);
  }

  public List<Board> findTopEntriesByGuildId(Long guildId, int limit) {
    return boardRepository.findTopByGuildId(guildId, limit);
  }

  public List<Board> findMaxLevelByGuildId(Long guildId) {
    return boardRepository.findMaxLevelByGuildId(guildId);
  }

  public List<Board> findMaxAll() {
    return boardRepository.findMaxLevelAll();
  }

  public List<Board> findUnprocessed() {
    return boardRepository.findUnprocessed();
  }

  public void setProcessed() {
    boardRepository.setProcessed();
  }

  public Optional<Board> getEntryForRemoval(String name, boolean global) {
    return boardRepository.getEntryForRemoval(name, global);
  }
}
