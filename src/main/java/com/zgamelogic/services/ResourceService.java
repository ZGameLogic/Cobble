package com.zgamelogic.services;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@DiscordController
public class ResourceService {
    private final ResourcePatternResolver resourcePatternResolver;
    private final ResourceLoader resourceLoader;
    private final HashMap<String, CustomEmoji> emojis;
    private final HashMap<String, ICommandReference> commands;
    private final List<String> maleFirstNames;
    private final List<String> femaleFirstNames;
    private final List<String> lastNames;
    @Getter
    private final Resource cobbleLogo;

    public ResourceService(ResourcePatternResolver resourcePatternResolver, ResourceLoader resourceLoader) throws IOException {
        this.resourcePatternResolver = resourcePatternResolver;
        this.resourceLoader = resourceLoader;
        emojis = new HashMap<>();
        commands = new HashMap<>();
        this.maleFirstNames = new LinkedList<>();
        this.femaleFirstNames = new LinkedList<>();
        this.lastNames = new LinkedList<>();
        cobbleLogo = resourceLoader.getResource("classpath:assets/Cobble/cobble-logo.png");
        loadNames();
    }

    private void loadNames() throws IOException {
        Scanner in = new Scanner(resourceLoader.getResource("classpath:assets/Cobble/female_first_names.txt").getInputStream());
        while (in.hasNextLine()) femaleFirstNames.add(in.nextLine());
        in.close();
        in = new Scanner(resourceLoader.getResource("classpath:assets/Cobble/male_first_names.txt").getInputStream());
        while (in.hasNextLine()) maleFirstNames.add(in.nextLine());
        in.close();
        in = new Scanner(resourceLoader.getResource("classpath:assets/Cobble/last_names.txt").getInputStream());
        while (in.hasNextLine()) lastNames.add(in.nextLine());
        in.close();
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
    public String cm(String name){
        return commands.containsKey(name) ? commands.get(name).getAsMention() : name;
    }

    public BufferedImage mapAppearance(String appearance) throws IOException {
        int index = 1;
        int skinColorOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int hairColorOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int hairStyleOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int eyeColorOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int facialHairOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int shirtColorOffset = Integer.parseInt(appearance.substring(index, (index++) + 1));
        int pantColorOffset = Integer.parseInt(appearance.substring(index, index + 1));
        List<Color> colors = List.of(
            new Color(0, 0, 0),
            new Color(189, 189, 189),
            new Color(138, 79, 15),
            new Color(246, 189, 113),
            new Color(250, 137, 43)
        );

        BufferedImage npc = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pane = npc.createGraphics();
        // skin
        BufferedImage skins = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/skin.png").getInputStream());
        pane.drawImage(skins.getSubimage(skinColorOffset * 32, 0, 32, 32), 0, 0, null);
        // shirt
        BufferedImage shirts = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/shirt.png").getInputStream());
        pane.drawImage(shirts.getSubimage(shirtColorOffset * 32, 0, 32, 32), 0, 0, null);
        // pants
        BufferedImage pants = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/pants.png").getInputStream());
        pane.drawImage(pants.getSubimage(pantColorOffset * 32, 0, 32, 32), 0, 0, null);
        // eyes
        BufferedImage eyes = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/eyes.png").getInputStream());
        pane.drawImage(eyes.getSubimage(eyeColorOffset * 32, 0, 32, 32), 0, 0, null);
        // hair
        BufferedImage hair = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/hair.png").getInputStream());
        hair = applyColorToWhite(hair.getSubimage(hairStyleOffset * 32, 0, 32, 32), colors.get(hairColorOffset));
        pane.drawImage(hair, 0, 0, null);
        // facial hair
        BufferedImage fhair = ImageIO.read(resourceLoader.getResource("classpath:/assets/cobble/npc assets/facial-hair.png").getInputStream());
        fhair = applyColorToWhite(fhair.getSubimage(facialHairOffset * 32, 0, 32, 32), colors.get(hairColorOffset));
        pane.drawImage(fhair, 0, 0, null);

        pane.dispose();
        int newWidth = npc.getWidth() * 3;
        int newHeight = npc.getHeight() * 3;
        BufferedImage scaledNpc = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledNpc.createGraphics();
        g2d.drawImage(npc, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return scaledNpc;
    }

    private BufferedImage applyColorToWhite(BufferedImage image, Color color) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                Color pixelColor = new Color(pixel, true);
                if (pixelColor.getRed() == 255 && pixelColor.getGreen() == 255 && pixelColor.getBlue() == 255 && pixelColor.getAlpha() > 0) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
        return image;
    }

    public InputStream mapAppearanceAsStream(String appearance) throws IOException {
        BufferedImage image = mapAppearance(appearance);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public String randomName(boolean male) {
        Random random = new Random();
        String firstName = male ?
            maleFirstNames.get(random.nextInt(maleFirstNames.size())) :
            femaleFirstNames.get(random.nextInt(femaleFirstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        return firstName + " " + lastName;
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
