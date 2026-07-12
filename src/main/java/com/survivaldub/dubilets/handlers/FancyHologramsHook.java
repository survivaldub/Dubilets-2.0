package com.survivaldub.dubilets.handlers;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class FancyHologramsHook implements DubiletHologram {
    
    private Hologram hologram;

    public FancyHologramsHook(String name, Location loc) {
        String holoName = "dubilet_" + name;
        HologramRegistry registry = FancyHolograms.get().getRegistry();

        registry.get(holoName).ifPresent(existing -> registry.unregister(existing));

        TextHologramData data = new TextHologramData(holoName, loc.clone().add(0.5, 1.6, 0.5));
        data.setBackground(org.bukkit.Color.fromARGB(100, 0, 0, 0));
        data.setFilePath(holoName);
        data.setVisibilityDistance(32);
        this.hologram = FancyHolograms.get().getHologramFactory().apply(data);
        registry.register(this.hologram);
    }

    @Override
    public void setText(List<String> lines) {
        if (this.hologram != null && this.hologram.getData() instanceof TextHologramData) {
            TextHologramData textData = (TextHologramData) this.hologram.getData();
            textData.setText(lines);
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.hologram.updateFor(player);
            }
        }
    }

    @Override
    public void delete() {
        if (this.hologram != null) {
            HologramRegistry registry = FancyHolograms.get().getRegistry();
            registry.unregister(this.hologram);
        }
    }
}
