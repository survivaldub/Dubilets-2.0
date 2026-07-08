package com.survivaldub.dubilets.handlers;

import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.database.models.PlayerModel;
import org.bukkit.entity.Player;

public class PlayerHandler {

    private final Dubilets plugin;
    private static PlayerHandler instance;

    public PlayerHandler(Dubilets plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public static PlayerHandler getInstance() {
        return instance;
    }

    public PlayerModel createPlayer(PlayerModel prePlayer) {
        this.executeChangesToPlayer(prePlayer, true);
        return Dubilets.getDataHandler().getPlayerByUUID(prePlayer.getUuid());
    }

    public int getPlayerDubets(Player player) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByUUID(player.getUniqueId());
        if (playerModel != null) {
            return playerModel.getDubets();
        }
        return -1;
    }

    public int getPlayerDubets(String nickname) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByNickname(nickname);
        if (playerModel != null) {
            return playerModel.getDubets();
        }
        return -1;
    }

    public void setPlayerDubets(Player player, int dubets) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByUUID(player.getUniqueId());
        if (playerModel != null) {
            playerModel.setDubets(dubets);
            this.executeChangesToPlayer(playerModel, false);
        } else {
            PlayerModel newModel = new PlayerModel(player.getUniqueId(), player.getName(), "es", 0L, 0.0, dubets, player.hasPermission("survivaldub.staff"), false);
            this.executeChangesToPlayer(newModel, true);
        }
    }

    public void setPlayerDubets(String nickname, int dubets) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByNickname(nickname);
        if (playerModel != null) {
            playerModel.setDubets(dubets);
            this.executeChangesToPlayer(playerModel, false);
        }
    }

    public void addPlayerDubets(Player player, int dubets) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByUUID(player.getUniqueId());
        if (playerModel != null) {
            playerModel.setDubets(playerModel.getDubets() + dubets);
            this.executeChangesToPlayer(playerModel, false);
        } else {
            PlayerModel newModel = new PlayerModel(player.getUniqueId(), player.getName(), "es", 0L, 0.0, dubets, player.hasPermission("survivaldub.staff"), false);
            this.executeChangesToPlayer(newModel, true);
        }
    }

    public void addPlayerDubets(String nickname, int dubets) {
        PlayerModel playerModel = Dubilets.getDataHandler().getPlayerByNickname(nickname);
        if (playerModel != null) {
            playerModel.setDubets(playerModel.getDubets() + dubets);
            this.executeChangesToPlayer(playerModel, false);
        }
    }

    private void executeChangesToPlayer(PlayerModel model, boolean newPlayer) {
        if (newPlayer) {
            Dubilets.getDataHandler().insertPlayer(model);
        } else {
            Dubilets.getDataHandler().updatePlayer(model);
        }
    }
}
