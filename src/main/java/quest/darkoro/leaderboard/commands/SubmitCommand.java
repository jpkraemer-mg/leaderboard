package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.ENABLED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ATTACHMENT;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
public class SubmitCommand implements BaseCommand {

  @Override
  public SlashCommandData create() {
    var a = List.of(
        new OptionData(STRING, "username", "Your username").setRequired(true),
        new OptionData(INTEGER, "level", "Your current level").setRequired(true),
        new OptionData(ATTACHMENT, "proof",
            "A screenshot of your current level as proof").setRequired(true)
    );
    return Commands.slash("submit", "Submit your level for the leaderboard")
        .addSubcommands(
            new SubcommandData("faction", "Submit your entry to the faction leaderboard")
                .addOptions(a)
        )
        .addSubcommands(
            new SubcommandData("global", "Submit your entry to the global leaderboard")
                .addOptions(a)
        )
        .setDefaultPermissions(ENABLED)
        .setGuildOnly(false);
  }
}
