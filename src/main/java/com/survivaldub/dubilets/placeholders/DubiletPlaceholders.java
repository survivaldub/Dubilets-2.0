package com.survivaldub.dubilets.placeholders;

import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.handlers.PlayerHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DubiletPlaceholders extends PlaceholderExpansion {

    private final Dubilets plugin;

    public DubiletPlaceholders(Dubilets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "survivaldub";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "dubilets";
    }

    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("dubets")) {
            int dubets = PlayerHandler.getInstance().getPlayerDubets(player);
            if (dubets < 0) {
                return "-";
            }
            return String.valueOf(dubets);
        }
        return null;
    }
}
