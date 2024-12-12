package quest.darkoro.leaderboard.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiController {

  @GetMapping("/methods")
  public String getAllowedMethods() {
    return "GET, POST, PUT, DELETE";
  }
}
