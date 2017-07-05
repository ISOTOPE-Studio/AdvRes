package cc.isotopestudio.advres.util;
/*
 * Created by Mars Tan on 7/5/2017.
 * Copyright ISOTOPE Studio
 */

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Util {
    public static String locationToString(Location loc) {
        if (loc == null) return "";
        return loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }

    public static Location stringToLocation(String string) {
        if (string == null) return null;
        String[] s = string.split(" ");
        if (s.length != 4) return null;
        World world = Bukkit.getWorld(s[0]);
        int x = Integer.parseInt(s[1]);
        int y = Integer.parseInt(s[2]);
        int z = Integer.parseInt(s[3]);
        return new Location(world, x + .5, y, z + .5);
    }
}
