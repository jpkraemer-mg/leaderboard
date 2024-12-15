package quest.darkoro.leaderboard.listener.button;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.TertiaryListener;

@Service
@Slf4j
@RequiredArgsConstructor
@TertiaryListener
public class UnknownButtonListener extends ListenerAdapter {

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged()) {
      return;
    }
    e.reply("Unknown button!").setEphemeral(true).queue();
  }
}
