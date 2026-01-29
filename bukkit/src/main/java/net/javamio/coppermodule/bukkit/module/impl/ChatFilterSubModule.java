package net.javamio.coppermodule.bukkit.module.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.javamio.coppermodule.bukkit.module.BukkitSubModule;
import net.javamio.coppermodule.common.module.Module;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChatFilterSubModule extends BukkitSubModule implements Listener {

    private final @NotNull List<String> blacklistedWords = List.of("fuck", "bad", "wtf");

    public ChatFilterSubModule(@NotNull Module parentModule, @NotNull JavaPlugin plugin) {
        super("chat-filter", "Chat Filter", parentModule, plugin);
    }

    @Override
    public void onEnable() throws ModuleException {
        registerListener(this);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();

        final String plainMessage = event.signedMessage().message();
        final String blacklistedWord = containsProfanity(plainMessage);

        if (blacklistedWord != null) {
            player.sendMessage(Component.text("You cannot say that word!").color(NamedTextColor.RED));
            event.setCancelled(true);
        }
    }

    private String containsProfanity(@NotNull String message) {
        String lowerCaseMessage = message.toLowerCase(Locale.ROOT);

        for (String word : blacklistedWords) {
            if (lowerCaseMessage.contains(word)) return word;
        }

        return blacklistedWords.stream()
                .filter(word -> word.length() > 3)
                .filter(word -> {
                    String pattern = String.join("\\s*[^a-zA-Z0-9]*\\s*", word.split("")).replaceAll("(.)", Pattern.quote("$1"));
                    return Pattern.compile(pattern).matcher(lowerCaseMessage).find();
                })
                .findFirst()
                .orElse(null);
    }
}
