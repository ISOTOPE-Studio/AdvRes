package cc.isotopestudio.advres;
/*
 * Created by Mars Tan on 7/5/2017.
 * Copyright ISOTOPE Studio
 */

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static cc.isotopestudio.advres.AdvRes.resData;

public abstract class BeaconUtil {
    public static void clearBeacon(Player player) {
        ItemStack[] inv = player.getInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
            String resName = isBeacon(player, inv[i]);
            if (resName == null) continue;
            if (!resData.getString(resName + ".player", "").equals(player.getName())) {
                player.getInventory().remove(inv[i]);
            }
        }
    }

    public static String isBeacon(HumanEntity player, ItemStack beacon) {
        if (beacon == null || beacon.getType() != Material.BEACON) return null;
        if (!beacon.hasItemMeta() || !beacon.getItemMeta().hasLore())
            return null;

        String resName = null;
        String playerName = null;
        for (String s : beacon.getItemMeta().getLore()) {
            s = ChatColor.stripColor(s);
            if (s.contains("领地: ")) {
                resName = s.replace("领地: ", "");
            } else if (s.contains("玩家: ")) {
                playerName = s.replace("玩家: ", "");
            }
        }
        if (player.getName().equals(playerName) && resName != null) return resName;
        return null;
    }
}
