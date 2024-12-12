package quest.darkoro.leaderboard.controllers;

import jakarta.validation.constraints.Min;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import quest.darkoro.leaderboard.api.BoardsApi;
import quest.darkoro.leaderboard.api.model.Board;
import quest.darkoro.leaderboard.api.model.BoardsQueryResult;
import quest.darkoro.leaderboard.persistence.BoardRepository;
import quest.darkoro.leaderboard.persistence.GuildRepository;
import quest.darkoro.leaderboard.utils.JpaApiMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoardController implements BoardsApi {

  private final GuildRepository guildRepository;
  private final BoardRepository boardRepository;

  @Override
  public ResponseEntity<BoardsQueryResult> apiBoardsGet(Optional<@Min(1) Integer> limit) {
    var boards = boardRepository.findAll();
    var result = new BoardsQueryResult();
    result.setItems(boards.stream().map(JpaApiMapper::toApi).toList());
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<Board> apiBoardsPost(Board board) {
    var guild = guildRepository.getGuildByGuildId(board.getGuildId());
    if (guild.isEmpty()) {
      return ResponseEntity.badRequest().body(board);
    }

    var savedBoard = boardRepository.save(JpaApiMapper.toJpa(board, null));
    return ResponseEntity.ok(JpaApiMapper.toApi(savedBoard));
  }
}
