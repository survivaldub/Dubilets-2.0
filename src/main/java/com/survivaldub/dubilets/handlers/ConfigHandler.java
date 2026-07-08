package com.survivaldub.dubilets.handlers;

import com.survivaldub.dubilets.Dubilets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigHandler {

    private Dubilets core;

    public ConfigHandler(Dubilets core) {
        this.core = core;
        this.loadConfig();
    }

    public void loadConfig() {
        File configFile;
        File pluginFolder = new File("plugins" + System.getProperty("file.separator") + "Dubilets");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        configFile = new File("plugins" + System.getProperty("file.separator") + "Dubilets" + System.getProperty("file.separator") + "config.yml");
        if (!configFile.exists()) {
            Dubilets.log.info("No config file found! Creating new one...");
            this.core.saveDefaultConfig();
        }
        try {
            this.core.getConfig().load(configFile);
            Dubilets.log.info("Config file loaded!");
        } catch (Exception var4) {
            Dubilets.log.info("Could not load config file! You need to regenerate the config!");
            var4.printStackTrace();
        }
    }

    public List<String> getStringList(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.core.getConfig().getStringList(key);
    }

    public String getString(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return "errorCouldNotLocateInConfigYml:" + key;
        }
        return this.core.getConfig().getString(key);
    }

    public boolean setString(String key, String value) throws IOException {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe(key + " doesn''t exist");
            return false;
        }
        this.core.getConfig().set(key, value);
        this.core.saveConfig();
        this.core.reloadConfig();
        return true;
    }

    public Integer getInteger(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.core.getConfig().getInt(key);
    }

    public Long getLong(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.core.getConfig().getLong(key);
    }

    public Double getDouble(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.core.getConfig().getDouble(key);
    }

    public boolean setDouble(String key, Double value) throws IOException {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe(key + " doesn''t exist");
            return false;
        }
        this.core.getConfig().set(key, value);
        this.core.saveConfig();
        this.core.reloadConfig();
        return true;
    }

    public Boolean getBoolean(String key) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.core.getConfig().getBoolean(key);
    }

    public boolean setStringToList(String key, List<String> list, String value) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return false;
        }
        list.add(value);
        this.core.getConfig().set(key, list);
        this.core.saveConfig();
        this.core.reloadConfig();
        return true;
    }

    public boolean removeStringFromList(String key, List<String> list, String value) {
        if (!this.core.getConfig().contains(key)) {
            this.core.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return false;
        }
        list.remove(value);
        this.core.getConfig().set(key, list);
        this.core.saveConfig();
        this.core.reloadConfig();
        return true;
    }

    public Map<String, Object> getMapFromSection(String key) {
        ConfigurationSection section = this.core.getConfig().getConfigurationSection(key);
        return section.getValues(false);
    }

    public ConfigurationSection getSection(String key) {
        return this.core.getConfig().getConfigurationSection(key);
    }

    public void createSectionWithMap(String key, Map<String, Object> map) {
        this.core.getConfig().createSection(key, map);
        this.core.saveConfig();
        this.core.reloadConfig();
    }

    public void removeSection(String key) {
        this.core.getConfig().set(key, null);
        this.core.saveConfig();
        this.core.reloadConfig();
    }
}
