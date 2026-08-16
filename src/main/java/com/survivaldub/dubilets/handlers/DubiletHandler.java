package com.survivaldub.dubilets.handlers;

import com.survivaldub.dubilets.DubiletConfig;
import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.handlers.models.Prize;
import com.survivaldub.dubilets.utils.BlockUtils;
import com.survivaldub.dubilets.utils.ChatUtils;
import com.survivaldub.dubilets.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DubiletHandler {

    private Dubilets plugin;
    private static final Sound SONG_SOUND = Sound.BLOCK_NOTE_BLOCK_HARP;
    private String name;
    private Location location;
    private DubiletHologram hologram;
    private boolean used;

    public DubiletHandler(Dubilets plugin) {
        this.plugin = plugin;
    }

    public DubiletHandler(Dubilets plugin, String name, Location loc) {
        this.plugin = plugin;
        this.name = name;
        this.location = loc;
        if (loc != null) {
            if (Bukkit.getPluginManager().isPluginEnabled("FancyHolograms")) {
                // La 3.x usa com.fancyinnovations y la 2.x de.oliver: se prueban las dos.
                try {
                    this.hologram = new FancyHologramsHook(name, loc);
                } catch (Throwable v3) {
                    try {
                        this.hologram = new FancyHologramsV2Hook(name, loc);
                    } catch (Throwable v2) {
                        this.plugin.getLogger().warning("FancyHolograms no sirve para " + name + " (v3: " + v3 + " | v2: " + v2 + "), se usa el holograma nativo.");
                    }
                }
            } else {
                this.plugin.getLogger().info("FancyHolograms no esta activo, se usa el holograma nativo para " + name + ".");
            }
            if (this.hologram == null) {
                try {
                    this.hologram = new NativeHologram(name, loc);
                } catch (Throwable t) {
                    this.plugin.getLogger().warning("No se pudo crear el holograma para " + name + ": " + t);
                }
            }
            try {
                this.setDefaultHologram();
            } catch (Throwable t) {
                this.plugin.getLogger().warning("No se pudo poner el texto del holograma " + name + ": " + t);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    private void setDefaultHologram() {
        if (this.hologram == null) {
            return;
        }
        ArrayList<String> lines = new ArrayList<>();
        List<String> configLines = this.plugin.getLanguageHandler().getStringList("dubilets.hologram");
        if (configLines == null || configLines.isEmpty()) {
            this.plugin.getLogger().warning("Falta 'dubilets.hologram' en lang.yml, usando el texto por defecto. Borra lang.yml para regenerarlo.");
            configLines = List.of("&b&l%name%", "&e¡Gasta aqui tus dubets!");
        }
        String customName = this.plugin.getConfigHandler().getString("custom-name");
        if (customName == null || customName.isEmpty() || customName.startsWith("errorCouldNotLocateInConfigYml")) {
            customName = "&bDUBILET";
        }
        for (String line : configLines) {
            lines.add(ChatUtils.translateColor(line.replace("%name%", customName)));
        }
        this.hologram.setText(lines);
    }

    public boolean prepare(Player player) {
        double totalProbability = 0.0;
        ArrayList<Prize> prizes = new ArrayList<>();
        if (this.plugin.getCoreConfig().getPrizes().isEmpty()) {
            player.sendMessage(ChatUtils.translateColor(this.plugin.getLanguageHandler().getString("dubilets.no_prizes")));
            return false;
        }
        for (Prize prize : this.plugin.getCoreConfig().getPrizes()) {
            if (prize.hasPlayerObtained(player)) continue;
            prizes.add(prize);
            totalProbability += prize.getPercent();
        }
        if (prizes.isEmpty() || totalProbability <= 0.0) {
            player.sendMessage(ChatUtils.translateColor("&cNo hay premios disponibles para ti en esta maquina."));
            this.used = false;
            return false;
        }
        double pos = MathUtils.getRandomNumber(0.0, totalProbability);
        double cumulative = 0.0;
        Prize finalPrize = null;
        for (Prize p : prizes) {
            cumulative += p.getPercent();
            if (pos > cumulative) continue;
            finalPrize = p;
            break;
        }
        if (finalPrize != null) {
            this.prepareAnimation(player, totalProbability, finalPrize);
            return true;
        }
        this.used = false;
        return false;
    }

    private void prepareAnimation(final Player player, final double totalProbability, final Prize prize) {
        if (this.hologram != null) {
            this.hologram.setText(new ArrayList<>());
        }
        final ArmorStand armorstand = this.createArmorstand(player);
        double percent = prize.getPercent();
        final Color color = percent >= 0.4 * totalProbability ? Color.AQUA
                : (percent >= 0.15 * totalProbability ? Color.LIME
                : (percent >= 0.05 * totalProbability ? Color.YELLOW
                : (percent >= 0.02 * totalProbability ? Color.ORANGE : Color.RED)));

        final DubiletHandler self = this;
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                try {
                    if (this.tick > 50) {
                        try {
                            armorstand.remove();
                            if (self.hologram != null) {
                                ArrayList<String> lines = new ArrayList<>();
                                lines.add(ChatUtils.translateColor(self.plugin.getLanguageHandler().getString("dubilets.player_won").replace("%player%", player.getName())));
                                lines.add(ChatUtils.translateColor(prize.getName()));
                                self.hologram.setText(lines);
                            }

                            final org.bukkit.entity.ItemDisplay itemDisplay = (org.bukkit.entity.ItemDisplay) self.location.getWorld().spawnEntity(self.location.clone().add(0.5, 1.3, 0.5), org.bukkit.entity.EntityType.ITEM_DISPLAY);
                            // Se pasa el icono entero, no solo el material, para conservar
                            // CustomModelData, encantamientos y textura de cabeza.
                            itemDisplay.setItemStack(prize.getIcon());
                            itemDisplay.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
                            org.bukkit.util.Transformation transformation = itemDisplay.getTransformation();
                            transformation.getScale().set(0.6f, 0.6f, 0.6f);
                            itemDisplay.setTransformation(transformation);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    itemDisplay.remove();
                                    self.setUsed(false);
                                    if (self.hologram != null) {
                                        self.setDefaultHologram();
                                    }
                                }
                            }.runTaskLater((Plugin) self.plugin, 65L);
                            player.sendMessage(ChatUtils.translateColor(self.plugin.getLanguageHandler().getString("dubilets.you_have_won").replace("%prize%", prize.getName())));
                            Bukkit.getLogger().info("[Dubilets]: " + player.getName() + " ganó " + ChatUtils.translateColor(prize.getName()));
                            for (String cmd : prize.getCommands()) {
                                if (cmd == null || cmd.isEmpty()) continue;
                                Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                            }
                            if (prize.getPermission() != null && self.plugin.getPerms() != null) {
                                self.plugin.getPerms().playerAdd(null, (OfflinePlayer) player, prize.getPermission());
                            }
                            BlockUtils.detonateFirework((Plugin) self.plugin, 1L, BlockUtils.spawnFirework(self.location.clone().add(0.5, 1.0, 0.5), 0, FireworkEffect.Type.BURST, color));
                            if (prize.getPercent() <= 0.05 * totalProbability) {
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, org.bukkit.SoundCategory.MASTER, 2.5f, 1.0f);
                            } else {
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, org.bukkit.SoundCategory.MASTER, 2.5f, 0.3f);
                            }
                        } finally {
                            this.cancel();
                        }
                        return;
                    }
                    if (this.tick % 4 == 0) {
                        self.playNote(this.tick, self.location);
                    }
                    Vector v = new Vector(0.0, -0.06, 0.0);
                    armorstand.teleport(armorstand.getLocation().add(v));
                    armorstand.setHeadPose(new EulerAngle(0.0, (double) this.tick / 1.5, 0.0));
                    ++this.tick;
                } catch (Exception e) {
                    self.plugin.getLogger().warning("Error en animacion Dubilet: " + e.getMessage());
                    e.printStackTrace();
                    this.cancel();
                    armorstand.remove();
                    self.setUsed(false);
                }
            }
        }.runTaskTimer((Plugin) this.plugin, 0L, 1L);
    }

    private ArmorStand createArmorstand(Player player) {
        ArmorStand as = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().add(0.5, 3.0, 0.5), EntityType.ARMOR_STAND);
        as.setSmall(true);
        as.setVisible(false);
        as.setGravity(false);
        as.setBasePlate(false);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            if (player.hasPermission("survivaldub.ultra")) {
                meta.setOwningPlayer((OfflinePlayer) player);
            } else {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer("Edna_I"));
            }
            head.setItemMeta((ItemMeta) meta);
        }
        as.getEquipment().setHelmet(head);
        return as;
    }

    private void playNote(int tick, Location location) {
        switch (tick) {
            case 0:
            case 12:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.12f);
                break;
            case 4:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 0.94f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.88f);
                break;
            case 8:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.06f);
                break;
            case 16:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.26f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 0.76f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.88f);
                break;
            case 20:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.06f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.12f);
                break;
            case 24:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.12f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.4f);
                break;
            case 28:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.26f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 0.84f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.88f);
                break;
            case 32:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.06f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.52f);
                break;
            case 36:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.4f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.12f);
                break;
            case 40:
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 1.88f);
                location.getWorld().playSound(location, SONG_SOUND, org.bukkit.SoundCategory.MASTER, 3.0f, 0.94f);
                break;
        }
    }

    public DubiletHandler getDubilet(Block block) {
        for (DubiletHandler dubiletHandler : DubiletConfig.getDubilets()) {
            if (!dubiletHandler.getLocation().getBlock().equals(block)) continue;
            return dubiletHandler;
        }
        return null;
    }

    public boolean addDubilet(Block block, String name) {
        if (this.getDubilet(block) != null) {
            return false;
        }
        DubiletConfig.getDubilets().add(new DubiletHandler(this.plugin, name, block.getLocation()));
        try {
            this.plugin.getCoreConfig().saveLocation("dubilets.locations", name, block.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean removeDubilet(String name) {
        Iterator<DubiletHandler> it = DubiletConfig.getDubilets().iterator();
        while (it.hasNext()) {
            DubiletHandler h2 = it.next();
            if (!h2.getName().equalsIgnoreCase(name)) continue;
            h2.destroy();
            try {
                this.plugin.getCoreConfig().saveLocation("dubilets.locations", name, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            it.remove();
            return true;
        }
        return false;
    }

    public void destroy() {
        if (this.hologram != null) {
            this.hologram.delete();
        }
    }
}
