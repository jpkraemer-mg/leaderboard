package quest.darkoro.leaderboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "quest.darkoro.leaderboard")
@Slf4j
public class LeaderboardApplication {

  public static void main(String[] args) {
    final int cores = Runtime.getRuntime().availableProcessors();
    if (cores <= 1) {
      log.info("Available Cores '{}', setting Parallelism Flag", cores);
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
    }
    SpringApplication.run(LeaderboardApplication.class, args);
    log.debug("Application started.");
  }
}
