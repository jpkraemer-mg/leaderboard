package quest.darkoro.leaderboard.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  public boolean deleteBoardByBoardId(UUID boardId) {
    return boardRepository.findBoardById(boardId)
        .map(board -> {
          boardRepository.delete(board);
          return true;
        })
        .orElse(false);
  }

  public Optional<Board> getBoardById(UUID boardId) {
    return boardRepository.findBoardById(boardId);
  }

  public Optional<Board> getBoardByGuildIdAndNameAndShared(Long guildId, String name, boolean shared) {
    return boardRepository.getBoardByGuildIdAndNameAndShared(guildId, name, shared);
  }

  public List<Board> findTopEntriesByGuildId(Long guildId, int limit) {
    return boardRepository.findTopByGuildId(guildId, limit, Board.MAX_LEVEL);
  }

  public List<Board> findMaxLevelByGuildId(Long guildId) {
    return boardRepository.findMaxLevelByGuildId(guildId, Board.MAX_LEVEL);
  }

  public List<Board> findTopAll(Integer limit) {
    return boardRepository.findTopAll(limit, Board.MAX_LEVEL);
  }

  public List<Board> findMaxAll() {
    return boardRepository.findMaxLevelAll(Board.MAX_LEVEL);
  }

  public List<Board> findUnprocessedShared() {
    return boardRepository.findUnprocessedShared();
  }

  public List<Board> findUnprocessedByGuildId(Long guildId) {
    return boardRepository.findUnprocessedByGuildId(guildId);
  }

  public boolean hasUnprocessedEntries() {
    return boardRepository.existsByPendingFalseAndProcessedFalse();
  }

  public void setProcessedShared() {
    boardRepository.setProcessedShared();
  }

  public void setProcessedByGuildId(Long guildId) {
    boardRepository.setProcessedByGuildId(guildId);
  }

  public Optional<Board> getEntryForRemoval(String name, boolean global) {
    return boardRepository.getEntryForRemoval(name, global);
  }

  public Optional<Board> getLocalEntryForRemoval(Long guildId, String name) {
    return boardRepository.getLocalEntryForRemoval(guildId, name);
  }

  /**
   * A global entry also counts for the local leaderboard of the guild it was submitted from.
   * Local entries are never mirrored to the global leaderboard.
   */
  public void mirrorGlobalToLocal(Board globalEntry) {
    if (!globalEntry.isShared() || globalEntry.isPending()) {
      return;
    }
    var local = getBoardByGuildIdAndNameAndShared(
        globalEntry.getGuildId(), globalEntry.getName(), false)
        .orElseGet(() -> new Board()
            .setGuildId(globalEntry.getGuildId())
            .setName(globalEntry.getName())
            .setShared(false));
    saveBoard(local
        .setLevel(globalEntry.getLevel())
        .setPending(false)
        .setProcessed(false));
  }
}
