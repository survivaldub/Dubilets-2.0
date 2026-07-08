package com.survivaldub.dubilets.menus.lib.actions;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface OpenAction {
    void onOpen(Player player);
}
