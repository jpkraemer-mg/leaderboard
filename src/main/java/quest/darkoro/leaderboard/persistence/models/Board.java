package quest.darkoro.leaderboard.persistence.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private int level;

  @JoinColumn(name = "guildId", foreignKey = @ForeignKey(name = "FK_BOARD_GUILD", foreignKeyDefinition = "FOREIGN KEY (guild_id) REFERENCES guild(guild_id)"))
  private Long guildId;

  @Column
  private String name;

  @Column
  private boolean shared;

  @Column
  private boolean pending;

  @Column
  private boolean processed = false;
}
