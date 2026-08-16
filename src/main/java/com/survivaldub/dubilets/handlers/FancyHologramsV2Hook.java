package com.survivaldub.dubilets.handlers;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;

/**
 * Hook para FancyHolograms 2.x (paquete de.oliver.fancyholograms).
 * La 3.x movio la API a com.fancyinnovations, por eso hay dos hooks.
 */
public class FancyHologramsV2Hook implements DubiletHologram {

    private Hologram hologram;

    public FancyHologramsV2Hook(String name, Location loc) {
        String holoName = "dubilet_" + name;
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

        manager.getHologram(holoName).ifPresent(manager::removeHologram);

        TextHologramData data = new TextHologramData(holoName, loc.clone().add(0.5, 1.6, 0.5));
        data.setBackground(Color.fromARGB(100, 0, 0, 0));
        data.setVisibilityDistance(48);
        // Sin persistir: lo recrea el plugin en cada arranque y no ensucia holograms.yml.
        data.setPersistent(false);

        this.hologram = manager.create(data);
        manager.addHologram(this.hologram);
    }

    @Override
    public void setText(List<String> lines) {
        if (this.hologram == null) {
            return;
        }
        if (this.hologram.getData() instanceof TextHologramData) {
            ((TextHologramData) this.hologram.getData()).setText(lines);
            this.hologram.forceUpdate();
            this.hologram.refreshForViewers();
        }
    }

    @Override
    public void delete() {
        if (this.hologram == null) {
            return;
        }
        this.hologram.deleteHologram();
        FancyHologramsPlugin.get().getHologramManager().removeHologram(this.hologram);
        this.hologram = null;
    }
}
