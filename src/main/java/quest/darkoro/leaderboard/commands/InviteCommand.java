package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.ENABLED;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
public class InviteCommand implements BaseCommand {

  @Override
  public CommandData create() {
    return Commands.slash("invite", "Invite the bot to your server!")
        .setGuildOnly(false)
        .setDefaultPermissions(ENABLED);
  }
}
