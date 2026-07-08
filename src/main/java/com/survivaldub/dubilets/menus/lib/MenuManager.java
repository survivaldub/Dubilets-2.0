package com.survivaldub.dubilets.menus.lib;

import com.survivaldub.dubilets.Dubilets;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener {

    private final Dubilets plugin;
    private final Map<Player, CoreMenu> menuMap;

    public MenuManager(Dubilets plugin) {
        this.plugin = plugin;
        this.menuMap = new HashMap<>();
    }

    public Map<Player, CoreMenu> getMenuMap() {
        return this.menuMap;
    }

    public boolean hasMenuOpen(Player player) {
        return this.menuMap.containsKey(player);
    }

    public boolean setPlayerMenu(Player player, CoreMenu menu) {
        CoreMenu openMenu = this.menuMap.get(player);
        if (openMenu != null) {
            openMenu.close(player);
        }
        if (menu != null) {
            menu.setMenuManager(this);
            menu.load(player);
            if (menu.display(player)) {
                this.menuMap.put(player, menu);
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            if (e.getCurrentItem() != null && e.getSlot() <= e.getInventory().getSize() && e.getWhoClicked() instanceof Player) {
                Player player = (Player) e.getWhoClicked();
                CoreMenu menu = this.menuMap.get(player);
                if (menu != null && e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
                    if (menu.clickOption(player, e.getClick(), e.getSlot())) {
                        e.setCancelled(true);
                        player.closeInventory();
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        CoreMenu menu = this.menuMap.get(player);
        if (e.getInventory().getHolder() == player && menu != null && menu.getInventory().getType() == e.getInventory().getType()) {
            this.menuMap.remove(player);
            if (menu instanceof NoCloseable) {
                menu.display(player);
            } else {
                menu.closeActions(player);
            }
        }
    }
}
