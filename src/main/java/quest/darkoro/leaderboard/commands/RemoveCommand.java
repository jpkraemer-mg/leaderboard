package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.ENABLED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
public class RemoveCommand implements BaseCommand {

  @Override
  public CommandData create() {
    var l = new OptionData(STRING, "username",
        "Username of the person you want removed").setRequired(true);
    return Commands.slash("remove", "Request the removal of a user from the leaderboard")
        .addSubcommands(new SubcommandData("faction",
            "Request the removal from the faction leaderboard")
            .addOptions(l))
        .addSubcommands(new SubcommandData("global",
            "Request the removal from the global leaderboard")
            .addOptions(l)
        )
        .setGuildOnly(false)
        .setDefaultPermissions(ENABLED);
  }
}
