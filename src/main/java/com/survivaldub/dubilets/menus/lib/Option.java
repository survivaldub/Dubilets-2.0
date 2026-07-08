package com.survivaldub.dubilets.menus.lib;

import com.survivaldub.dubilets.menus.lib.actions.ClickAction;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Option {

    private static final ClickAction DEFAULT_ACTION = (player, click) -> false;
    private int slot;
    private ItemStack icon;
    private ClickAction action = DEFAULT_ACTION;

    public Option(int slot, ItemStack icon) {
        this.slot = slot;
        this.setIcon(icon);
        this.setEmptyName();
    }

    public Option(int slot, Material material) {
        if (material == null) {
            material = Material.ENDER_EYE;
        }
        this.slot = slot;
        this.setIcon(new ItemStack(material));
        this.setEmptyName();
    }

    public int getSlot() { return this.slot; }
    public void setSlot(int slot) { this.slot = slot; }
    public ItemStack getIcon() { return this.icon; }

    public void setIcon(ItemStack icon) {
        this.icon = icon.clone();
    }

    public ClickAction getAction() { return this.action; }

    public Option setAction(ClickAction action) {
        this.action = action;
        return this;
    }

    public Option setMaterial(Material material) {
        if (material == null) material = Material.STONE;
        this.icon.setType(material);
        return this;
    }

    public Option setAmount(int amount) {
        this.icon.setAmount(amount);
        return this;
    }

    public Option setName(String name) {
        ItemMeta meta = this.icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            this.icon.setItemMeta(meta);
        }
        return this;
    }

    public Option setEmptyName() {
        ItemMeta meta = this.icon.getItemMeta();
        if (meta != null) {
            if (!meta.hasDisplayName()) {
                meta.setDisplayName(" ");
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            this.icon.setItemMeta(meta);
        }
        return this;
    }

    public Option setDefaultName() {
        ItemMeta meta = this.icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(null);
            meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            this.icon.setItemMeta(meta);
        }
        return this;
    }

    public Option setDescription(List<String> lore) {
        ItemMeta meta = this.icon.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            this.icon.setItemMeta(meta);
        }
        return this;
    }

    public Option setDescription(String line) {
        ItemMeta meta = this.icon.getItemMeta();
        ArrayList<String> lore = new ArrayList<>(1);
        lore.add(line);
        if (meta != null) {
            meta.setLore(lore);
            this.icon.setItemMeta(meta);
        }
        return this;
    }

    public Option setEnchantment(boolean enchant) {
        Enchantment glowEnchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("infinity"));
        if (glowEnchant == null) {
            // Fallback: try unbreaking if infinity not available
            glowEnchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking"));
        }
        if (enchant) {
            ItemMeta meta = this.icon.getItemMeta();
            if (meta != null && glowEnchant != null) {
                meta.addEnchant(glowEnchant, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                this.icon.setItemMeta(meta);
            }
        } else if (!this.icon.getEnchantments().isEmpty()) {
            for (Enchantment enchantment : this.icon.getEnchantments().keySet()) {
                this.icon.removeEnchantment(enchantment);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        String iconString = "none";
        if (this.icon != null) {
            iconString = this.icon.hasItemMeta() ? this.icon.getItemMeta().getDisplayName() : this.icon.getType().name();
        }
        return "Option [slot=" + this.slot + ", icon=" + iconString + "]";
    }
}
