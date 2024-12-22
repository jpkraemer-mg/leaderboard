package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.Permission.MANAGE_SERVER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
public class ConfigureCommand implements BaseCommand {

  @Override
  public SlashCommandData create() {
    return Commands.slash("configure", "Configure the bot")
        .addSubcommands(
            new SubcommandData("server", "Configure the leaderboard settings for this server")
                .addOptions(
                    new OptionData(CHANNEL, "leaderboards",
                        "What channel will the leaderboards be posted in?")
                        .setRequired(true)
                )
                .addOptions(
                    new OptionData(CHANNEL, "submissions",
                        "What channel will the submissions be posted in?")
                        .setRequired(true)
                )
                .addOptions(
                    new OptionData(ROLE, "permitted", "Who can accept submissions?")
                        .setRequired(true)
                )
                .addOptions(new OptionData(INTEGER, "limit",
                    "How many people should the leaderboard show at once?"))
        )
        .addSubcommands(
            new SubcommandData("profile", "Configure the bot profile for this server")
                .addOptions(
                    new OptionData(STRING, "name", "What should the bots name be?")
                        .setRequired(true)
                )
        )
        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(ADMINISTRATOR, MANAGE_SERVER))
        .setGuildOnly(false);
  }
}
