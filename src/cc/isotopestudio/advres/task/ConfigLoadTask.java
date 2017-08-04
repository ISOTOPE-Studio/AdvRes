package cc.isotopestudio.advres.task;
/*
 * Created by david on 2017/8/4.
 * Copyright ISOTOPE Studio
 */

import cc.isotopestudio.advres.AdvRes;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static cc.isotopestudio.advres.AdvRes.config;
import static cc.isotopestudio.advres.AdvRes.playerData;

public class ConfigLoadTask extends BukkitRunnable {
    public final static Map<String, Integer> PLAYERBROKEMAP = new HashMap<>();

    @Override
    public void run() {
        PLAYERBROKEMAP.clear();
        AdvRes.BEACONBREAKCOUNT = config.getInt("count", 20);
        AdvRes.ANNOUNCEMENTMSG = config.getString("announcement", "");
        for (String playerName : playerData.getKeys(false)) {
            int broke = playerData.getInt(playerName + ".broke");
            if (broke > 0) {
                PLAYERBROKEMAP.put(playerName, broke);
            }
        }

    }
}
