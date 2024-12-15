package quest.darkoro.leaderboard.persistence.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Guild {

  @Id
  private Long guildId;

  @Column
  private String name;

  @Column
  private Long channelId;

  @Column
  private Long submissionChannelId;

  @Column
  private Long permitted;
}
