package quest.darkoro.leaderboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "quest.darkoro.leaderboard")
@Slf4j
public class LeaderboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(LeaderboardApplication.class, args);
    log.debug("Application started.");
  }
}
