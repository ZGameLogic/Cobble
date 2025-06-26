package com.zgamelogic.controllers;

import com.zgamelogic.discord.annotations.DiscordController;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.context.annotation.Bean;

import java.util.List;

@DiscordController
public class CobbleListener {
    @Bean
    public List<CommandData> cobbleCommands(){
        return List.of(
            Commands.slash("help", "Get some idea on how to play the game."),
            Commands.slash("start", "Start the game of cobble!")
                .addOption(OptionType.STRING, "town-name", "The name of your town", false),
            Commands.slash("production", "Get an overview of production statistics for your town"),
            Commands.slash("citizens", "Get information on citizens in your town")
                .addOption(OptionType.STRING, "citizen", "Get a specific citizen", false, true),
            Commands.slash("building", "building stuff").addSubcommands(
                new SubcommandData("codex", "Get a book of buildings and what they do")
                    .addOption(OptionType.STRING, "building", "Start the book on a specific building", false, true)
            ),
            Commands.slash("rename", "rename stuff").addSubcommands(
                new SubcommandData("building", "Rename a building")
                    .addOption(OptionType.STRING, "name", "The current name of the building", true, true)
                    .addOption(OptionType.STRING, "new-name", "The new name of the building", true),
                new SubcommandData("town", "Rename your town")
                    .addOption(OptionType.STRING, "new-name", "The new name of the town", true)
            ),
            Commands.slash("schedule", "Schedule things to happen").addSubcommands(
                new SubcommandData("build", "Schedule a building to be built during the day")
                    .addOption(OptionType.STRING, "building", "The building to be scheduled to be built", true, true)
                    .addOption(OptionType.STRING, "npc", "The NPC to assign to this building when built", true, true),
                new SubcommandData("upgrade", "Schedule a building to be upgraded during the day")
                    .addOption(OptionType.STRING, "building", "The building to be scheduled to be upgraded", true, true)
                    .addOption(OptionType.STRING, "npc", "The NPC to assign to this building after upgrade if required", false, true)
            )
        );
    }
}
