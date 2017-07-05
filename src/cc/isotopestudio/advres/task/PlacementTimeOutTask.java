package cc.isotopestudio.advres.task;
/*
 * Created by Mars Tan on 7/4/2017.
 * Copyright ISOTOPE Studio
 */

import cc.isotopestudio.advres.AdvRes;
import cc.isotopestudio.advres.util.S;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cc.isotopestudio.advres.AdvRes.resData;
import static cc.isotopestudio.advres.BeaconUtil.clearBeacon;

public class PlacementTimeOutTask extends BukkitRunnable {
    @Override
    public void run() {
        for (String resName : resData.getKeys(false)) {
            if (resData.getBoolean(resName + ".finished")) continue;
            long time = resData.getLong(resName + ".time");
            int sec = (int) ((System.currentTimeMillis() - time) / 1000);
            System.out.println(resName + ": " + sec);
            if (sec >= 10 * 60) {
                ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(resName);
                if (res != null) {
                    res.remove();
                    String playerName = resData.getString(resName + ".player");
                    double cost = resData.getDouble(resName + ".cost");
                    AdvRes.econ.depositPlayer(Bukkit.getOfflinePlayer(playerName), cost);
                    Player player = Bukkit.getPlayerExact(playerName);
                    if (player != null) {
                        player.sendMessage(S.toPrefixRed("领地创建失败"));
                        clearBeacon(player);
                    }
                }
                resData.set(resName, null);
                resData.save();
            }
        }
    }

}
