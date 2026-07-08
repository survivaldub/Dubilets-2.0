package com.survivaldub.dubilets.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryUtils {

    public static ItemStack parseIconItem(String string) {
        if (string == null || string.isEmpty()) {
            return new ItemStack(Material.STONE);
        }
        String[] split = string.split(":");
        Material material = Material.matchMaterial(split[0]);
        if (material == null) {
            material = Material.STONE;
        }
        int amount = 1;
        if (split.length > 1) {
            amount = Integer.parseInt(split[1]);
        }
        ItemStack item = new ItemStack(material, amount);
        if (split.length > 3) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatUtils.translateColor(split[3].replace("_", " ")));
                item.setItemMeta(meta);
            }
        }
        if (split.length > 4) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                ArrayList<String> lore = new ArrayList<>();
                for (int i = 4; i < split.length; ++i) {
                    lore.add(ChatUtils.translateColor(split[i].replace("_", " ")));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    public static ItemStack createItem(Material material, int amount, short data, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            if (lore != null) {
                ArrayList<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(coloredLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void setTextureUrlMeta(SkullMeta meta, String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            return;
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
    }
}
