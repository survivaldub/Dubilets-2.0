package com.survivaldub.dubilets.handlers;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.List;

/**
 * Holograma nativo con TextDisplay (1.19.4+). Se usa cuando FancyHolograms no
 * esta instalado o su API no coincide con la que espera el plugin, para que el
 * dubilet tenga texto siempre y no dependa de un plugin externo.
 */
public class NativeHologram implements DubiletHologram {

    private static final String TAG_PREFIX = "dubilet_holo_";

    private final String tag;
    private final Location location;
    private TextDisplay display;

    public NativeHologram(String name, Location loc) {
        this.tag = TAG_PREFIX + name;
        this.location = loc.clone().add(0.5, 1.6, 0.5);
        this.removeStale();
        this.spawn();
    }

    /** Borra restos de arranques anteriores para no acumular hologramas. */
    private void removeStale() {
        for (Entity entity : this.location.getWorld().getNearbyEntities(this.location, 3.0, 3.0, 3.0)) {
            if (entity.getScoreboardTags().contains(this.tag)) {
                entity.remove();
            }
        }
    }

    private void spawn() {
        this.display = (TextDisplay) this.location.getWorld().spawnEntity(this.location, EntityType.TEXT_DISPLAY);
        this.display.addScoreboardTag(this.tag);
        this.display.setBillboard(Display.Billboard.CENTER);
        this.display.setBackgroundColor(Color.fromARGB(100, 0, 0, 0));
        this.display.setViewRange(1.0f);
    }

    @Override
    public void setText(List<String> lines) {
        if (this.display == null || this.display.isDead()) {
            this.spawn();
        }
        this.display.setText(String.join("\n", lines));
    }

    @Override
    public void delete() {
        if (this.display != null) {
            this.display.remove();
            this.display = null;
        }
        this.removeStale();
    }
}
