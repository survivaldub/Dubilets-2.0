package com.survivaldub.dubilets.menus.lib.actions;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface CloseAction {
    void onClose(Player player);
}
