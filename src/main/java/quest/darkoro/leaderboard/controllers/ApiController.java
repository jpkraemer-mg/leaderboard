package quest.darkoro.leaderboard.controllers;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import quest.darkoro.leaderboard.api.BoardsApi;
import quest.darkoro.leaderboard.api.GuildsApi;
import quest.darkoro.leaderboard.api.model.Board;
import quest.darkoro.leaderboard.api.model.BoardsQueryResult;
import quest.darkoro.leaderboard.api.model.Guild;
import quest.darkoro.leaderboard.api.model.GuildsQueryResult;
import quest.darkoro.leaderboard.persistence.BoardRepository;
import quest.darkoro.leaderboard.persistence.GuildRepository;
import quest.darkoro.leaderboard.utils.JpaApiMapper;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiController implements GuildsApi, BoardsApi {
    private final GuildRepository guildRepository;
    private final BoardRepository boardRepository;

    @GetMapping("/methods")
    public String getAllowedMethods() {
        return "GET, POST, PUT, DELETE";
    }

    @Override
    public ResponseEntity<GuildsQueryResult> apiGuildsGet(Optional<@Min(1) Integer> limit) {
        var guilds = guildRepository.findAll();
        var result = new GuildsQueryResult();
        result.setItems(guilds.stream().map(JpaApiMapper::toApi).toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Guild> apiGuildsPost(Guild guild) {
        var savedGuild = guildRepository.save(JpaApiMapper.toJpa(guild, null));
        return ResponseEntity.ok(JpaApiMapper.toApi(savedGuild));
    }

    @Override
    public ResponseEntity<BoardsQueryResult> apiBoardsGet(Optional<@Min(1) Integer> limit) {
        var boards = boardRepository.findAll();
        var result = new BoardsQueryResult();
        result.setItems(boards.stream().map(JpaApiMapper::toApi).toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Board> apiBoardsPost(Board board) {
        var savedBoard = boardRepository.save(JpaApiMapper.toJpa(board, null));
        return ResponseEntity.ok(JpaApiMapper.toApi(savedBoard));
    }
}
