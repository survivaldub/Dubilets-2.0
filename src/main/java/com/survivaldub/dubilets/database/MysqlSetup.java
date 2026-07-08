package com.survivaldub.dubilets.database;

import com.survivaldub.dubilets.Dubilets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlSetup {

    private Dubilets plugin;
    private Connection conn = null;
    private int connectAttempt = 0;

    public MysqlSetup(Dubilets plugin) {
        this.plugin = plugin;
        this.setupDatabase();
    }

    public boolean setupDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.plugin.getConfigHandler().getString("database.host") + ":" + this.plugin.getConfigHandler().getString("database.port") + "/",
                    this.plugin.getConfigHandler().getString("database.user"),
                    this.plugin.getConfigHandler().getString("database.password"));
            Statement stmt = this.conn.createStatement();
            String sql = "CREATE SCHEMA IF NOT EXISTS " + this.plugin.getConfigHandler().getString("database.name");
            stmt.executeUpdate(sql);
            stmt.close();
            this.conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.plugin.getConfigHandler().getString("database.host") + ":" + this.plugin.getConfigHandler().getString("database.port") + "/" + this.plugin.getConfigHandler().getString("database.name"),
                    this.plugin.getConfigHandler().getString("database.user"),
                    this.plugin.getConfigHandler().getString("database.password"));
            this.setupTable();
            ++this.connectAttempt;
            this.plugin.getLogger().info("Mysql connection successful!");
            return true;
        } catch (Exception sqlE) {
            this.plugin.getLogger().warning("Could not connect to mysql database!\n" + sqlE.getMessage());
            return false;
        }
    }

    public void setupTable() {
        try (Statement query = this.conn.createStatement()) {
            String data = "CREATE TABLE IF NOT EXISTS " + this.plugin.getConfigHandler().getString("database.name") +
                    ".`Player` (`uuid` varchar(36) NOT NULL, `nickname` varchar(16) NOT NULL, `language` varchar(2) NOT NULL, `discordId` BIGINT NULL, `coins` FLOAT NOT NULL, `dubets` INT NOT NULL, `isStaff` BIT NOT NULL, `isDgi` BIT NOT NULL, PRIMARY KEY(uuid));";
            query.executeUpdate(data);
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Error creating tables! Error: " + e.getMessage());
        }
    }

    public boolean closeConnection() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public synchronized boolean reConnect() {
        try {
            long start = System.currentTimeMillis();
            this.plugin.getLogger().info("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.plugin.getConfigHandler().getString("database.host") + ":" + this.plugin.getConfigHandler().getString("database.port") + "/" + this.plugin.getConfigHandler().getString("database.name"),
                    this.plugin.getConfigHandler().getString("database.user"),
                    this.plugin.getConfigHandler().getString("database.password"));
            long end = System.currentTimeMillis();
            this.plugin.getLogger().info("Connection to MySQL server established!");
            this.plugin.getLogger().info("Connection took " + (end - start) + "ms!");
            return true;
        } catch (Exception e) {
            this.plugin.getLogger().warning("Could not connect to MySQL server! because: " + e.getMessage());
            return false;
        }
    }

    public boolean checkConnection() {
        try {
            if (this.conn == null) {
                this.plugin.getLogger().warning("Connection failed. Reconnecting...");
                return this.reConnect();
            }
            if (!this.conn.isValid(3)) {
                this.plugin.getLogger().warning("Connection is idle or terminated. Reconnecting...");
                return this.reConnect();
            }
            if (this.conn.isClosed()) {
                this.plugin.getLogger().warning("Connection is closed. Reconnecting...");
                return this.reConnect();
            }
            return true;
        } catch (Exception e) {
            this.plugin.getLogger().warning("Could not reconnect to Database!");
            return true;
        }
    }

    public Connection getConnection() {
        this.checkConnection();
        return this.conn;
    }
}
