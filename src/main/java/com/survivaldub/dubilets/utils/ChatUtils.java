package com.survivaldub.dubilets.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatUtils {

    @SuppressWarnings("deprecation")
    public static String translateColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void broadcastMessage(Collection<? extends Player> players, String messageKey, String placeholder, String value) {
        String message = ChatUtils.getMessageFromConfig(messageKey).replace(placeholder, value);
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    private static String getMessageFromConfig(String messageKey) {
        return "Default message";
    }
}
