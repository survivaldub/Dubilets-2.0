package com.survivaldub.dubilets.menus.lib.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface ClickAction {
    boolean onClick(Player player, ClickType click);
}
