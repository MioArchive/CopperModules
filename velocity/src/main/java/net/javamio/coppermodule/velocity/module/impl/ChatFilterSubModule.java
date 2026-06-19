package net.javamio.coppermodule.velocity.module.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.javamio.coppermodule.common.module.Module;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.javamio.coppermodule.velocity.module.VelocitySubModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChatFilterSubModule extends VelocitySubModule {

    private final @NotNull List<String> blacklistedWords = List.of("fuck", "bad", "wtf");

    public ChatFilterSubModule(@NotNull Module parentModule, @NotNull Object plugin, @NotNull ProxyServer server) {
        super("chat-filter", "Chat Filter", parentModule, plugin, server);
    }

    @Override
    public void onEnable() throws ModuleException {
        registerListener(this);
    }

    @Subscribe
    @SuppressWarnings("deprecation")
    public void onPlayerChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();

        final String plainMessage = event.getMessage();
        final String blacklistedWord = containsProfanity(plainMessage);

        if (blacklistedWord != null) {
            player.sendMessage(Component.text("You cannot say that word!").color(NamedTextColor.RED));
            event.setResult(PlayerChatEvent.ChatResult.denied());
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
