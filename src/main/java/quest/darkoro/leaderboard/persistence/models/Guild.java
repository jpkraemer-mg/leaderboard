package quest.darkoro.leaderboard.persistence.models;

import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

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

  public Mono<?> then(InteractionApplicationCommandCallbackReplyMono reply) {
    return reply;
  }
}
