package cc.isotopestudio.advres.task;
/*
 * Created by Mars Tan on 7/6/2017.
 * Copyright ISOTOPE Studio
 */

import org.bukkit.scheduler.BukkitRunnable;

import static cc.isotopestudio.advres.AdvRes.resData;

public class BeaconHealTask extends BukkitRunnable {

    @Override
    public void run() {
        for (String resName : resData.getKeys(false)) {
            if (!resData.getBoolean(resName + ".finished")) continue;
            int count = resData.getInt(resName + ".break");
            if (count < 1) continue;
            resData.set(resName + ".break", --count);
            resData.save();
        }
    }

}

