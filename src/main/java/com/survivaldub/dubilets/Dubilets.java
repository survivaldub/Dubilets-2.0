package com.survivaldub.dubilets;

import com.survivaldub.dubilets.commands.DubiletsCommand;
import com.survivaldub.dubilets.database.MysqlSetup;
import com.survivaldub.dubilets.database.SqlDataHandler;
import com.survivaldub.dubilets.handlers.ConfigHandler;
import com.survivaldub.dubilets.handlers.DubiletHandler;
import com.survivaldub.dubilets.handlers.LanguageHandler;
import com.survivaldub.dubilets.handlers.PlayerHandler;
import com.survivaldub.dubilets.listeners.DubiletsListener;
import com.survivaldub.dubilets.menus.lib.MenuManager;
import com.survivaldub.dubilets.placeholders.DubiletPlaceholders;
import com.survivaldub.dubilets.utils.ChatUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Dubilets extends JavaPlugin {

    private static Dubilets instance;
    public static Logger log;
    public static Map<String, Integer> gameServersStats = new HashMap<>();
    private static DubiletConfig config;
    private static LanguageHandler languageHandler;
    private static ConfigHandler configHandler;
    private static ChatUtils chatUtils;
    private static PlayerHandler playerHandler;
    private static MenuManager menuManager;
    private static MysqlSetup mySqlSetup;
    private static SqlDataHandler dataHandler;
    private Permission perms;
    private static DubiletHandler dubiletHandler;
    private String serverName;

    public void initializer() {
        log = this.getLogger();
        configHandler = new ConfigHandler(this);
        languageHandler = new LanguageHandler(this);
        config = new DubiletConfig(this);
        chatUtils = new ChatUtils();
        playerHandler = new PlayerHandler(this);
        menuManager = new MenuManager(this);
        dubiletHandler = new DubiletHandler(this);
        mySqlSetup = new MysqlSetup(this);
        dataHandler = new SqlDataHandler(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        this.initializer();
        DubiletsCommand command = new DubiletsCommand(this);
        this.getCommand("dubilets").setExecutor((CommandExecutor) command);
        this.getCommand("dubilets").setTabCompleter((TabCompleter) command);
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents((Listener) new DubiletsListener(this), (Plugin) this);
        DubiletConfig.loadDubilets();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new DubiletPlaceholders(this).register();
        }
    }

    @Override
    public void onDisable() {
    }

    public static Dubilets getInstance() {
        return instance;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public DubiletConfig getCoreConfig() {
        return config;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public static MysqlSetup getMySqlSetup() {
        return mySqlSetup;
    }

    public static SqlDataHandler getDataHandler() {
        return dataHandler;
    }

    public static MenuManager getMenuManager() {
        return menuManager;
    }

    public DubiletHandler getDubiletHandler() {
        return dubiletHandler;
    }

    public DubiletHandler initDubiletHandler(String name, Location loc) {
        return new DubiletHandler(Dubilets.getInstance(), name, loc);
    }

    public Permission getPerms() {
        return this.perms;
    }
}
