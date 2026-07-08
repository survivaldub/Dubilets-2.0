package com.survivaldub.dubilets.handlers.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Prize {

    private final String name;
    private final ItemStack icon;
    private final PrizeCategory category;
    private final double percent;
    private final List<String> commands;
    private final String permission;

    public Prize(String name, double percent, ItemStack icon, PrizeCategory category, List<String> commands, String permission) {
        this.name = name;
        this.icon = icon;
        this.category = category;
        this.percent = percent;
        this.commands = commands;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public PrizeCategory getCategory() {
        return this.category;
    }

    public double getPercent() {
        return this.percent;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPlayerObtained(Player player) {
        if (this.permission != null) {
            return player.hasPermission(this.permission);
        }
        return false;
    }
}
