package com.survivaldub.dubilets.menus;

import com.survivaldub.dubilets.DubiletConfig;
import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.handlers.models.Prize;
import com.survivaldub.dubilets.menus.lib.CoreMenu;
import com.survivaldub.dubilets.menus.lib.Sizeable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DubiletInfoMenu extends CoreMenu implements Sizeable {

    @Override
    public void load(Player player) {
        List<Prize> prizes = DubiletConfig.getDubiletPrizes();
        int totalProbability = 0;
        int realTotalProbability = 0;
        for (Prize prize : prizes) {
            if (!prize.hasPlayerObtained(player)) {
                realTotalProbability = (int) ((double) realTotalProbability + prize.getPercent());
            }
            totalProbability = (int) ((double) totalProbability + prize.getPercent());
        }
        int i = 0;
        for (Prize prize : prizes) {
            ArrayList<String> lore = new ArrayList<>();
            boolean hasUnlocked = prize.hasPlayerObtained(player);
            if (hasUnlocked) {
                lore.add(ChatColor.RED + "YA DESBLOQUEADO");
            }
            lore.add("");
            String actualProbability = String.format("%.4f", prize.getPercent() / (double) totalProbability * 100.0) + "%";
            String realProbability = String.format("%.4f", prize.getPercent() / (double) realTotalProbability * 100.0) + "%";
            lore.add(ChatColor.AQUA + "Probabilidad actual: " + ChatColor.YELLOW + actualProbability);
            lore.add(ChatColor.AQUA + "Probabilidad real: " + ChatColor.GOLD + realProbability);
            String prizeName = ChatColor.translateAlternateColorCodes('&', prize.getName());
            player.sendMessage(ChatColor.AQUA + "- " + prizeName + ChatColor.AQUA + " : Probabilidad actual " + ChatColor.YELLOW + actualProbability + ChatColor.GREEN + " y real " + realProbability);
            this.addOption(i++, prize.getIcon()).setName(prizeName).setDescription(lore).setEnchantment(!hasUnlocked);
        }
    }

    @Override
    protected String getName(Player player) {
        return Dubilets.getInstance().getLanguageHandler().getString("menus.dubilets.info");
    }

    @Override
    public int getRows() {
        return 6;
    }
}
