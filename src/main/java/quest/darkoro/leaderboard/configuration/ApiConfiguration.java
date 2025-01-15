package quest.darkoro.leaderboard.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiConfiguration {
  @Bean
  public OpenAPI defineOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Leaderboard API")
            .version("0.6.0")
            .description("Access the leaderboard entries via API")
            .contact(new Contact().email("jean.kraemer@csbme.de").name("Darkoro"))
        );
  }
}
