package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.Permission.MANAGE_SERVER;
import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigureCommand implements BaseCommand {

  @Override
  public SlashCommandData create() {
    return Commands.slash("configure", "Configure the bot")
        .addSubcommands(
            new SubcommandData("server", "Configure the leaderboard settings for this server")
                .addOptions(
                    new OptionData(
                        CHANNEL, "leaderboards", "What channel will the leaderboards be posted in?"
                    )
                        .setDescriptionLocalization(GERMAN, "In welchem Kanal sollen die Bestenlisten veröffentlicht werden?")
                        .setNameLocalization(GERMAN, "bestenlisten")
                        .setRequired(true)
                )
                .addOptions(
                    new OptionData(
                        CHANNEL, "submissions", "What channel will the submissions be posted in?"
                    )
                        .setDescriptionLocalization(GERMAN,
                            "In welchem Kanal sollen die Einreichungen vermerkt werden?")
                        .setNameLocalization(GERMAN, "einreichungen")
                        .setRequired(true)
                )
                .addOptions(
                    new OptionData(
                        ROLE, "permitted", "Who can accept submissions?"
                    )
                        .setNameLocalization(GERMAN, "berechtigt")
                        .setDescriptionLocalization(GERMAN, "Wer kann Einreichungen akzeptieren?")
                        .setRequired(true)
                )
                .addOptions(
                    new OptionData(
                        INTEGER, "limit", "How many people should the leaderboard show at once?"
                    )
                        .setDescriptionLocalization(GERMAN, "Wie viele Leute sollen in einer Bestenliste angezeigt werden?")
                )
                .setDescriptionLocalization(GERMAN, "Konfiguriere den Server für den Bestenliste-Bot")
                .setNameLocalization(GERMAN, "konfiguration")
        )
        .addSubcommands(
            new SubcommandData("profile", "Configure the bot profile for this server")
                .addOptions(
                    new OptionData(
                        STRING, "name", "What should the bots name be?"
                    )
                        .setDescriptionLocalization(GERMAN, "Wie soll der Bot heißen?")
                        .setRequired(true)
                )
        )
        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(ADMINISTRATOR, MANAGE_SERVER))
        .setGuildOnly(false);
  }
}
