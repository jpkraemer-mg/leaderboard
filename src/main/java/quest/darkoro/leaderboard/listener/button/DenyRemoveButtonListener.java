package quest.darkoro.leaderboard.listener.button;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import quest.darkoro.leaderboard.annotations.SecondaryListener;

@Service
@Slf4j
@RequiredArgsConstructor
@SecondaryListener
public class DenyRemoveButtonListener extends ListenerAdapter {

  @Override
  public void onButtonInteraction(ButtonInteractionEvent e) {
    if (e.isAcknowledged() || !e.getButton().getId().equals("deny_remove")) {
      return;
    }
    e.reply("Not yet implemented.").setEphemeral(true).queue();
  }

}
