package cc.isotopestudio.advres.task;
/*
 * Created by david on 2017/8/4.
 * Copyright ISOTOPE Studio
 */

import cc.isotopestudio.advres.AdvRes;
import cc.isotopestudio.advres.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cc.isotopestudio.advres.AdvRes.resData;

public class AnnouncementTask extends BukkitRunnable {
    private List<String> resList = new ArrayList<>();

    @Override
    public void run() {
        resList.clear();
        resList.addAll(resData.getKeys(false)
                .stream().filter(s -> resData.getBoolean(s + ".finished")).collect(Collectors.toList()));
        if (resList.size() > 0) {
            Collections.shuffle(resList);
            Location loc = Util.stringToLocation(resData.getString(resList.get(0) + ".location"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    AdvRes.ANNOUNCEMENTMSG.replaceAll("<location>",
                            "X: " + loc.getBlockX()
                                    + " Y: " + loc.getBlockY()
                                    + " Z: " + loc.getBlockZ())));
        }
    }
}
