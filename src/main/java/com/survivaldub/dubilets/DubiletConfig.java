package com.survivaldub.dubilets;

import com.survivaldub.dubilets.handlers.DubiletHandler;
import com.survivaldub.dubilets.handlers.models.Prize;
import com.survivaldub.dubilets.handlers.models.PrizeCategory;
import com.survivaldub.dubilets.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class DubiletConfig {

    private Dubilets plugin;
    private static final List<DubiletHandler> dubilets = new LinkedList<>();
    private static final List<Prize> dubiletPrizes = new LinkedList<>();

    public DubiletConfig(Dubilets plugin) {
        this.plugin = plugin;
    }

    public List<Prize> getPrizes() {
        ArrayList<Prize> dubiletPrizes = new ArrayList<>();
        ConfigurationSection prizesSection = this.plugin.getConfigHandler().getSection("dubilets.prizes");
        if (prizesSection != null) {
            for (String key : prizesSection.getKeys(false)) {
                ConfigurationSection prizeSection = prizesSection.getConfigurationSection(key);
                if (prizeSection == null) {
                    Bukkit.getLogger().warning("[Dubilets] El premio '" + key + "' esta mal formado en config.yml, se ignora.");
                    continue;
                }
                String name = prizeSection.getString("name");
                double percent = prizeSection.getDouble("probability");
                ItemStack icon;
                Object rawIcon = prizeSection.get("icon");
                if (rawIcon instanceof ItemStack) {
                    // setPrize() guarda el ItemStack serializado, no una cadena; getString() daba null.
                    icon = (ItemStack) rawIcon;
                } else {
                    if (rawIcon == null) {
                        Bukkit.getLogger().warning("[Dubilets] El premio '" + key + "' no tiene 'icon' en config.yml.");
                    }
                    icon = InventoryUtils.parseIconItem(prizeSection.getString("icon"));
                }
                PrizeCategory category = PrizeCategory.get(prizeSection.getString("category"));
                List<String> commands = prizeSection.getStringList("commands");
                String permission = prizeSection.getString("permission");
                dubiletPrizes.add(new Prize(name, percent, icon, category, commands, permission));
            }
        }
        return dubiletPrizes;
    }

    public void setPrize(Prize prize) {
        int length = 1;
        List<Prize> prizes = this.plugin.getCoreConfig().getPrizes();
        if (!prizes.isEmpty()) {
            length = prizes.size() + 1;
        }
        HashMap<String, Object> prizeMap = new HashMap<>();
        prizeMap.put("name", prize.getName());
        prizeMap.put("probability", prize.getPercent());
        prizeMap.put("icon", prize.getIcon());
        prizeMap.put("category", prize.getCategory().getString());
        prizeMap.put("commands", prize.getCommands());
        this.plugin.getConfigHandler().createSectionWithMap("dubilets.prizes." + length, prizeMap);
    }

    public static Location getLocation(String key) {
        World world = Bukkit.getWorld((String) Dubilets.getInstance().getConfigHandler().getString(key + ".world"));
        return new Location(world,
                Dubilets.getInstance().getConfigHandler().getDouble(key + ".x"),
                Dubilets.getInstance().getConfigHandler().getDouble(key + ".y"),
                Dubilets.getInstance().getConfigHandler().getDouble(key + ".z"),
                Dubilets.getInstance().getConfigHandler().getDouble(key + ".yaw").floatValue(),
                Dubilets.getInstance().getConfigHandler().getDouble(key + ".pitch").floatValue());
    }

    public void saveLocation(String key, String name, Location location) throws IOException {
        ConfigurationSection section = this.plugin.getConfigHandler().getSection(key);
        if (location == null) {
            this.plugin.getConfigHandler().removeSection(key + "." + name);
        } else if (section != null && section.getKeys(false).contains(name)) {
            this.plugin.getConfigHandler().setString(key + "." + name + ".world", location.getWorld().getName());
            this.plugin.getConfigHandler().setDouble(key + "." + name + ".x", location.getX());
            this.plugin.getConfigHandler().setDouble(key + "." + name + ".y", location.getY());
            this.plugin.getConfigHandler().setDouble(key + "." + name + ".z", location.getZ());
            this.plugin.getConfigHandler().setDouble(key + "." + name + ".yaw", (double) location.getYaw());
            this.plugin.getConfigHandler().setDouble(key + "." + name + ".pitch", (double) location.getPitch());
        } else {
            Map<String, Object> dubiletConfig = DubiletConfig.setDubiletConfig(location);
            this.plugin.getConfigHandler().createSectionWithMap(key + "." + name, dubiletConfig);
        }
    }

    @NotNull
    private static Map<String, Object> setDubiletConfig(Location location) {
        HashMap<String, Object> dubiletConfig = new HashMap<>();
        dubiletConfig.put("world", location.getWorld().getName());
        dubiletConfig.put("x", location.getX());
        dubiletConfig.put("y", location.getY());
        dubiletConfig.put("z", location.getZ());
        dubiletConfig.put("yaw", (double) location.getYaw());
        dubiletConfig.put("pitch", (double) location.getPitch());
        return dubiletConfig;
    }

    public static void loadDubilets() {
        dubilets.clear();
        ConfigurationSection dubiletSection = Dubilets.getInstance().getConfigHandler().getSection("dubilets.locations");
        if (dubiletSection != null) {
            for (String key : dubiletSection.getKeys(false)) {
                dubilets.add(new DubiletHandler(Dubilets.getInstance(), key, DubiletConfig.getLocation("dubilets.locations." + key)));
            }
        }
    }

    public static List<DubiletHandler> getDubilets() {
        return dubilets;
    }

    public static List<Prize> getDubiletPrizes() {
        return dubiletPrizes;
    }
}
