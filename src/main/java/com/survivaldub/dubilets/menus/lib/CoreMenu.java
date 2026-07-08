package com.survivaldub.dubilets.menus.lib;

import com.survivaldub.dubilets.menus.lib.actions.CloseAction;
import com.survivaldub.dubilets.menus.lib.actions.OpenAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class CoreMenu {

    private MenuManager menuManager;
    private Inventory inventory;
    private final List<Option> options = new LinkedList<>();
    private int maxSlot;
    private String title;

    protected abstract String getName(Player player);

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public List<Option> getOptions() { return this.options; }
    public int getMaxSlot() { return this.maxSlot; }
    public Inventory getInventory() { return this.inventory; }

    public Option addOption(Option option) {
        this.options.add(option);
        if (option.getSlot() > this.maxSlot) {
            this.maxSlot = option.getSlot();
        }
        if (this.inventory != null && !this.inventory.getViewers().isEmpty()) {
            if (option.getSlot() < this.inventory.getSize()) {
                this.inventory.setItem(option.getSlot(), option.getIcon());
            } else {
                Bukkit.getLogger().warning("Error loading option out of inventory " + this.title + " (" + option + ")");
            }
        }
        return option;
    }

    public Option addOption(int slot, ItemStack icon) {
        return this.addOption(new Option(slot, icon));
    }

    public Option addOption(int slot, Material material) {
        return this.addOption(new Option(slot, material));
    }

    public boolean clickOption(Player player, ClickType click, int slot) {
        for (Option option : this.options) {
            if (option.getSlot() != slot || option.getAction() == null) continue;
            return option.getAction().onClick(player, click);
        }
        return false;
    }

    public boolean display(Player player) {
        String name = this.getName(player);
        if (this.options.isEmpty()) {
            return false;
        }
        int size;
        if (this instanceof Sizeable) {
            size = 9 * ((Sizeable) this).getRows();
        } else {
            size = 9 * this.getDefaultRows();
        }
        if (name.length() > 32) {
            name = name.substring(0, 31);
        }
        this.title = name;
        this.inventory = Bukkit.createInventory((InventoryHolder) player, size, this.title);
        for (Option o : this.options) {
            if (o.getSlot() < size && o.getSlot() >= 0) {
                this.inventory.setItem(o.getSlot(), o.getIcon());
            } else {
                Bukkit.getLogger().warning("Error loading option out of inventory " + this.title + " (" + o + ")");
            }
        }
        if (this instanceof Background) {
            ItemStack item = ((Background) this).getBackgroundItem();
            for (int i = 0; i < size; ++i) {
                if (this.inventory.getItem(i) != null) continue;
                this.inventory.setItem(i, item);
            }
        }
        if (this instanceof OpenAction) {
            ((OpenAction) this).onOpen(player);
        }
        player.openInventory(this.inventory);
        return true;
    }

    public abstract void load(Player player);

    protected int getDefaultRows() {
        return this.maxSlot / 9 + 1;
    }

    public void updatePosition(Option option, int slot) {
        this.removeIcon(slot);
        this.inventory.setItem(option.getSlot(), null);
        option.setSlot(slot);
        this.inventory.setItem(slot, option.getIcon());
    }

    public boolean updatePosition(int oldSlot, int newSlot) {
        for (Option option : this.options) {
            if (option.getSlot() != oldSlot) continue;
            this.updatePosition(option, newSlot);
            return true;
        }
        return false;
    }

    public void updateIcon(int slot, ItemStack icon) {
        for (Option option : this.options) {
            if (option.getSlot() != slot) continue;
            option.setIcon(icon);
            this.inventory.setItem(slot, icon);
            break;
        }
    }

    public void removeIcon(int slot) {
        Iterator<Option> it = this.options.iterator();
        while (it.hasNext()) {
            int aux = it.next().getSlot();
            if (aux != slot) continue;
            this.inventory.setItem(slot, null);
            it.remove();
            break;
        }
    }

    public void removeOption(Option option) {
        Iterator<Option> it = this.options.iterator();
        while (it.hasNext()) {
            Option aux = it.next();
            if (aux != option) continue;
            this.inventory.setItem(option.getSlot(), null);
            it.remove();
            break;
        }
    }

    public void clearOptions() {
        Iterator<Option> it = this.options.iterator();
        while (it.hasNext()) {
            Option option = it.next();
            this.inventory.setItem(option.getSlot(), null);
            it.remove();
        }
    }

    public void enchantOption(Option option) {
        for (Option opt : this.options) {
            if (opt != option) continue;
            ItemStack item = option.getIcon();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) break;
            Enchantment glowEnchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("infinity"));
            if (glowEnchant != null) {
                meta.addEnchant(glowEnchant, 1, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            option.setIcon(item);
            if (this.inventory == null) break;
            this.inventory.setItem(option.getSlot(), item);
            break;
        }
    }

    public void updateDisplayName(int slot, String name) {
        for (Option option : this.options) {
            if (option.getSlot() != slot) continue;
            option.setName(name);
            ItemStack item = this.inventory.getItem(slot);
            if (item == null) break;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) break;
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            break;
        }
    }

    public void reloadForAllViewers() {
        this.menuManager.getMenuMap().forEach((player, menu) -> {
            if (menu.equals(this)) {
                menu.load(player);
                menu.display(player);
            }
        });
    }

    public void reloadForOtherViewers(Player actual) {
        this.menuManager.getMenuMap().forEach((player, menu) -> {
            if (menu.equals(this) && !player.equals(actual)) {
                menu.load(player);
                menu.display(player);
            }
        });
    }

    public boolean isClosed() {
        return this.inventory.getViewers().isEmpty();
    }

    public void closeAll() {
        for (HumanEntity viewer : new ArrayList<>(this.inventory.getViewers())) {
            viewer.closeInventory();
            if (viewer instanceof Player) {
                this.closeActions((Player) viewer);
            }
        }
    }

    public void close(Player player) {
        player.closeInventory();
        this.closeActions(player);
    }

    public void closeActions(Player player) {
        if (this instanceof CloseAction) {
            ((CloseAction) this).onClose(player);
        }
    }

    public boolean changeMenu(Player player, CoreMenu menu) {
        this.menuManager.setPlayerMenu(player, menu);
        return false;
    }
}
