package quest.darkoro.leaderboard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quest.darkoro.leaderboard.persistence.BoardRepository;
import quest.darkoro.leaderboard.persistence.models.Board;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private GuildService guildService;

  @InjectMocks
  private BoardService boardService;

  private Board globalEntry(long guildId, String name, int level) {
    return new Board()
        .setId(UUID.randomUUID())
        .setGuildId(guildId)
        .setName(name)
        .setLevel(level)
        .setShared(true)
        .setPending(false);
  }

  @Test
  void mirrorGlobalToLocal_createsLocalEntryWhenNoneExists() {
    when(boardRepository.getBoardByGuildIdAndNameAndShared(1L, "player", false))
        .thenReturn(Optional.empty());

    boardService.mirrorGlobalToLocal(globalEntry(1L, "player", 500));

    var captor = ArgumentCaptor.forClass(Board.class);
    verify(boardRepository).save(captor.capture());
    var saved = captor.getValue();
    assertEquals(1L, saved.getGuildId());
    assertEquals("player", saved.getName());
    assertEquals(500, saved.getLevel());
    assertFalse(saved.isShared());
    assertFalse(saved.isPending());
    assertFalse(saved.isProcessed());
  }

  @Test
  void mirrorGlobalToLocal_overwritesExistingLocalEntry() {
    var existing = new Board()
        .setId(UUID.randomUUID())
        .setGuildId(1L)
        .setName("player")
        .setLevel(999)
        .setShared(false)
        .setPending(false)
        .setProcessed(true);
    when(boardRepository.getBoardByGuildIdAndNameAndShared(1L, "player", false))
        .thenReturn(Optional.of(existing));

    boardService.mirrorGlobalToLocal(globalEntry(1L, "player", 500));

    var captor = ArgumentCaptor.forClass(Board.class);
    verify(boardRepository).save(captor.capture());
    var saved = captor.getValue();
    assertEquals(existing.getId(), saved.getId());
    assertEquals(500, saved.getLevel());
    assertFalse(saved.isShared());
    assertFalse(saved.isProcessed());
  }

  @Test
  void mirrorGlobalToLocal_ignoresLocalEntries() {
    var local = globalEntry(1L, "player", 500).setShared(false);

    boardService.mirrorGlobalToLocal(local);

    verifyNoInteractions(boardRepository);
  }

  @Test
  void mirrorGlobalToLocal_ignoresPendingEntries() {
    var pending = globalEntry(1L, "player", 500).setPending(true);

    boardService.mirrorGlobalToLocal(pending);

    verifyNoInteractions(boardRepository);
  }
}
