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

  public ResponseEntity<Object> deleteBoardByBoardId(UUID boardId) {
    return boardRepository.findBoardById(boardId)
        .map(board -> {
          boardRepository.delete(board);
          return ResponseEntity.noContent().build();
        })
        .orElse(ResponseEntity.accepted().build());
  }
}
