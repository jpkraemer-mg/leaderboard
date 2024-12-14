package quest.darkoro.leaderboard.services;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.persistence.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final GuildService guildService;

  public ResponseEntity<Void> deleteBoardByBoardId(UUID boardId) {
    var board = boardRepository.findBoardById(boardId);
    if (board.isPresent()) {
      board.ifPresent(boardRepository::delete);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }
}
