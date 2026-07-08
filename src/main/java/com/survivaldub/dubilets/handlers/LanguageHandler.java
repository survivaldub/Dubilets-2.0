package com.survivaldub.dubilets.handlers;

import com.survivaldub.dubilets.Dubilets;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageHandler {

    private Dubilets plugin;
    private File file;
    private FileConfiguration fileConfig;

    public LanguageHandler(Dubilets plugin) {
        this.plugin = plugin;
        this.loadFile();
    }

    public void loadFile() {
        this.file = new File(this.plugin.getDataFolder(), "lang.yml");
        if (!this.file.exists()) {
            Dubilets.log.info("No lang.yml found! Creating new one...");
            this.file.getParentFile().mkdirs();
            this.plugin.saveResource("lang.yml", false);
        }
        this.fileConfig = new YamlConfiguration();
        try {
            this.fileConfig.load(this.file);
            Dubilets.log.info("lang.yml loaded!");
        } catch (Exception e) {
            Dubilets.log.info("Could not load lang.yml file! You need to regenerate lang.yml!");
            e.printStackTrace();
        }
    }

    public List<String> getStringList(String key) {
        if (!this.fileConfig.contains(key)) {
            this.plugin.getLogger().severe("Could not locate '" + key + "' in the lang.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.fileConfig.getStringList(key);
    }

    public String getString(String key) {
        if (!this.fileConfig.contains(key)) {
            this.plugin.getLogger().severe("Could not locate '" + key + "' in the lang.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return "errorCouldNotLocateInConfigYml:" + key;
        }
        return this.fileConfig.getString(key);
    }

    public Integer getInteger(String key) {
        if (!this.fileConfig.contains(key)) {
            this.plugin.getLogger().severe("Could not locate '" + key + "' in the lang.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.fileConfig.getInt(key);
    }

    public Boolean getBoolean(String key) {
        if (!this.fileConfig.contains(key)) {
            this.plugin.getLogger().severe("Could not locate '" + key + "' in the lang.yml inside of the Dubilets folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return this.fileConfig.getBoolean(key);
    }
}
