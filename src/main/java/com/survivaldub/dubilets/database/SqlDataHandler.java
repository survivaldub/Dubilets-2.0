package com.survivaldub.dubilets.database;

import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.database.models.PlayerModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlDataHandler {

    private Dubilets plugin;
    private Connection conn;

    public SqlDataHandler(Dubilets plugin) {
        this.plugin = plugin;
    }

    public PlayerModel getPlayerByUUID(UUID uuid) {
        PreparedStatement preparedStatement = null;
        PlayerModel model = null;
        try {
            this.conn = Dubilets.getMySqlSetup().getConnection();
            String sql = "SELECT * FROM `Player` WHERE `uuid` = ?";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                model = new PlayerModel(UUID.fromString(result.getString(1)), result.getString(2), result.getString(3), result.getLong(4), result.getDouble(5), result.getInt(6), result.getBoolean(7), result.getBoolean(8));
            }
            result.close();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error en DB: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    public PlayerModel getPlayerByNickname(String nickname) {
        PreparedStatement preparedStatement = null;
        PlayerModel model = null;
        try {
            this.conn = Dubilets.getMySqlSetup().getConnection();
            String sql = "SELECT * FROM `Player` WHERE `nickname` = ?";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setString(1, nickname);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                model = new PlayerModel(UUID.fromString(result.getString(1)), result.getString(2), result.getString(3), result.getLong(4), result.getDouble(5), result.getInt(6), result.getBoolean(7), result.getBoolean(8));
            }
            result.close();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error en DB: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    public List<PlayerModel> getPlayers() {
        PreparedStatement preparedStatement = null;
        ArrayList<PlayerModel> list = new ArrayList<>();
        try {
            this.conn = Dubilets.getMySqlSetup().getConnection();
            String sql = "SELECT * FROM `Player`";
            preparedStatement = this.conn.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                list.add(new PlayerModel(UUID.fromString(result.getString(1)), result.getString(2), result.getString(3), result.getLong(4), result.getDouble(5), result.getInt(6), result.getBoolean(7), result.getBoolean(8)));
            }
            result.close();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error en DB: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public boolean insertPlayer(PlayerModel model) {
        PreparedStatement preparedStatement = null;
        try {
            this.conn = Dubilets.getMySqlSetup().getConnection();
            String sql = "INSERT INTO `Player` (`uuid`, `nickname`, `language`, `discordId`, `coins`, `dubets`, `isStaff`, `isDgi`) VALUES (?,?,?,?,?,?,?,?)";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setString(1, model.getUuid().toString());
            preparedStatement.setString(2, model.getNickname());
            preparedStatement.setString(3, model.getLanguage());
            preparedStatement.setLong(4, model.getDiscordId());
            preparedStatement.setDouble(5, model.getCoins());
            preparedStatement.setInt(6, model.getDubets());
            preparedStatement.setBoolean(7, model.getStaff());
            preparedStatement.setBoolean(8, model.getDgi());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error en DB: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void updatePlayer(PlayerModel model) {
        PreparedStatement preparedStatement = null;
        try {
            this.conn = Dubilets.getMySqlSetup().getConnection();
            String sql = "UPDATE `Player` SET `dubets`=? WHERE `uuid`=?";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setInt(1, model.getDubets());
            preparedStatement.setString(2, model.getUuid().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error en DB: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
