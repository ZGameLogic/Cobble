package com.zgamelogic.services;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@DiscordController
public class ResourceService {
    private final ResourcePatternResolver resourcePatternResolver;

    private final HashMap<String, CustomEmoji> emojis;
    private final HashMap<String, ICommandReference> commands;

    public ResourceService(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
        emojis = new HashMap<>();
        commands = new HashMap<>();
    }

    /**
     * Get the mentionable for an emoji by name
     * @param name name of the emoji
     * @return String mentionable of an emoji if it exists
     */
    public String em(String name){
        return emojis.containsKey(name) ? emojis.get(name).getAsMention() : name;
    }

    /**
     * Get the mentionable for an command by name
     * @param name name of the command
     * @return String mentionable of an command if it exists
     */
    public String ec(String name){
        return commands.containsKey(name) ? commands.get(name).getAsMention() : name;
    }

    @DiscordMapping
    private void onReady(ReadyEvent event) throws IOException {
        JDA bot = event.getJDA();
        mapEmojis(bot);
        mapCommands(bot);
    }

    private void mapCommands(JDA bot) {
        bot.retrieveCommands().complete().forEach(command -> {
            command.getSubcommandGroups().forEach(group ->
                group.getSubcommands().forEach(subcommand ->
                    commands.put(subcommand.getFullCommandName(), subcommand)
                )
            );
            command.getSubcommands().forEach(subcommand ->
                commands.put(subcommand.getFullCommandName(), subcommand)
            );
            commands.put(command.getName(), command);
        });
    }

    private void mapEmojis(JDA bot) throws IOException {
        List<ApplicationEmoji> foundEmojis = new ArrayList<>(bot.retrieveApplicationEmojis().complete());
        Arrays.stream(resourcePatternResolver.getResources("classpath:assets/cobble/emojis/*")).forEach(resource -> {
            String filename = resource.getFilename();
            if(filename == null) return;
            String iconName = filename.replace(".png", "");
            Optional<ApplicationEmoji> foundEmoji = foundEmojis.stream().filter(e -> e.getName().equals(iconName)).findFirst();
            foundEmoji.ifPresentOrElse(e -> {
                emojis.put(e.getName(), e);
                foundEmojis.remove(e);
            }, () -> {
                log.info("Uploading emoji {} to application", iconName);
                try {
                    Icon icon = Icon.from(resource.getInputStream());
                    CustomEmoji e = bot.createApplicationEmoji(iconName, icon).complete();
                    emojis.put(e.getName(), e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        // Remove old emojis
        foundEmojis.forEach(emoji -> {
            log.info("Removing unused emoji {}", emoji.getName());
            emoji.delete().queue();
        });
    }
}
