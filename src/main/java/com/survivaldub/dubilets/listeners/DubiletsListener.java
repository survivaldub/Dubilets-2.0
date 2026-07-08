package com.survivaldub.dubilets.listeners;

import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.handlers.DubiletHandler;
import com.survivaldub.dubilets.handlers.PlayerHandler;
import com.survivaldub.dubilets.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

public class DubiletsListener implements Listener {

    private final Dubilets plugin;

    public DubiletsListener(Dubilets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (Dubilets.getDataHandler().getPlayerByUUID(player.getUniqueId()) == null) {
            com.survivaldub.dubilets.database.models.PlayerModel model = new com.survivaldub.dubilets.database.models.PlayerModel(
                    player.getUniqueId(), player.getName(), "es", 0L, 0.0, 0, player.hasPermission("survivaldub.staff"), false
            );
            PlayerHandler.getInstance().createPlayer(model);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && this.isBlockSimilarTo(e.getClickedBlock(), Material.END_PORTAL_FRAME)) {
            Player player = e.getPlayer();
            DubiletHandler dubiletHandler = this.plugin.getDubiletHandler().getDubilet(e.getClickedBlock());
            if (dubiletHandler != null) {
                if (!dubiletHandler.isUsed()) {
                    dubiletHandler.setUsed(true);
                    int dubets = PlayerHandler.getInstance().getPlayerDubets(player);
                    if (dubets > 0) {
                        if (dubiletHandler.prepare(player)) {
                            PlayerHandler.getInstance().addPlayerDubets(player, -1);
                        }
                    } else {
                        dubiletHandler.setUsed(false);
                        player.sendMessage(ChatUtils.translateColor(this.plugin.getLanguageHandler().getString("dubilets.no_dubets")));
                    }
                } else {
                    player.sendMessage(ChatUtils.translateColor(this.plugin.getLanguageHandler().getString("dubilets.wait_use")));
                }
            }
        }
    }

    private boolean isBlockSimilarTo(Block block, Material targetMaterial) {
        if (block == null) {
            return false;
        }
        return block.getType() == targetMaterial;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {
            e.setCancelled(true);
        }
    }
}
