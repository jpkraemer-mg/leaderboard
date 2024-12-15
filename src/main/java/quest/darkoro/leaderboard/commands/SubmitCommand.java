package quest.darkoro.leaderboard.commands;

import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;
import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.ENABLED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.BOOLEAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;
import quest.darkoro.leaderboard.interfaces.BaseCommand;

@Component
@RequiredArgsConstructor
public class SubmitCommand implements BaseCommand {

  @Override
  public SlashCommandData create() {
    return Commands.slash("submit", "Submit your level for the leaderboard")
        .addOptions(
            new OptionData(
                STRING, "username", "Your username"
            )
                .setNameLocalization(GERMAN, "nutzername")
                .setDescriptionLocalization(GERMAN, "Dein Benutzername")
                .setRequired(true)
        )
        .addOptions(
            new OptionData(
                INTEGER, "level", "Your current level"
            )
                .setDescriptionLocalization(GERMAN, "Dein aktuelles Level")
                .setRequired(true)
        )
        .addOptions(
            new OptionData(
                BOOLEAN, "shared", "Is this submission for the shared (public) leaderboard?"
            )
                .setNameLocalization(GERMAN, "geteilt")
                .setDescriptionLocalization(GERMAN,
                    "Ist diese Einreichung für die geteilte (öffentliche) Bestenliste?")
                .setRequired(false)
        )
        .setDefaultPermissions(ENABLED)
        .setNameLocalization(GERMAN, "einreichen")
        .setDescriptionLocalization(GERMAN, "Reiche dein Level für die Bestenliste ein")
        .setGuildOnly(false);
  }
}
