package cc.isotopestudio.advres.listener;
/*
 * Created by Mars Tan on 7/4/2017.
 * Copyright ISOTOPE Studio
 */

import cc.isotopestudio.advres.AdvRes;
import cc.isotopestudio.advres.util.S;
import cc.isotopestudio.advres.util.Util;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static cc.isotopestudio.advres.AdvRes.plugin;
import static cc.isotopestudio.advres.AdvRes.resData;
import static cc.isotopestudio.advres.BeaconUtil.isBeacon;

public class ResListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onResCreation(ResidenceCreationEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        String resName = event.getResidenceName();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ResidenceApi.getResidenceManager().getByName(resName) == null) return;
                resData.set(resName + ".time", System.currentTimeMillis());
                double cost = event.getPhysicalArea().getSize() *
                        ResidenceApi.getPlayerManager().getResidencePlayer(player.getName())
                                .getGroup().getCostPerBlock();
                resData.set(resName + ".cost", cost);
                resData.set(resName + ".player", player.getName());
                resData.set(resName + ".finished", false);
                resData.save();
                ItemStack beacon = new ItemStack(Material.BEACON);
                List<String> lore = new ArrayList<>();
                lore.add(S.toBoldGold("领地: " + resName));
                lore.add(S.toBoldGold("玩家: " + player.getName()));
                ItemMeta meta = beacon.getItemMeta();
                meta.setLore(lore);
                meta.setDisplayName(S.toBoldGreen("放置此信标来完成领地创建"));
                beacon.setItemMeta(meta);
                if (player.getInventory().getItemInMainHand() == null
                        || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    player.getInventory().setItemInMainHand(beacon);
                } else {
                    player.getInventory().addItem(beacon);
                }
                player.sendMessage(S.toPrefixYellow("已给予" + player.getName() +
                        "一个领地信标，请在十分钟以内将其放置在领地内，否则领地将会被删除"));
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        ItemStack beacon = event.getItem();
        String resName = isBeacon(player, beacon);
        if (resName != null && playerName != null) {
            event.setCancelled(true);
            if (!resData.getString(resName + ".player", "").equals(player.getName()) ||
                    resData.getBoolean(resName + ".finished")) {
                player.getInventory().remove(beacon);

            } else {
                ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(resName);
                if (res == null) {
                    player.getInventory().remove(beacon);
                    player.sendMessage(S.toPrefixRed("出现错误"));
                    return;
                }
                org.bukkit.block.Block block = event.getClickedBlock();
                if (block == null) return;
                if (res.getMainArea().containsLoc(block.getLocation())) {
                    event.setCancelled(false);
                } else {
                    player.sendMessage(S.toPrefixRed("请放置在领地内"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlacedBeacon(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        String resName = isBeacon(player, event.getItemInHand());
        if (resName == null) return;
        Location loc = event.getBlockPlaced().getLocation();
        loc.clone().add(0, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, 1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(-1, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(-1, -1, 1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(-1, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, 1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        resData.set(resName + ".finished", true);
        resData.set(resName + ".break", 0);
        resData.set(resName + ".location", Util.locationToString(loc));
        resData.save();
        player.getInventory().remove(event.getItemInHand());
        player.sendMessage(S.toPrefixRed("成功"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack beacon = event.getCurrentItem();
        String resName = isBeacon(player, event.getCurrentItem());
        if (resName == null) return;
        if (!resData.getString(resName + ".player", "").equals(player.getName()) ||
                resData.getBoolean(resName + ".finished")) {
            player.getInventory().remove(beacon);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() == Material.BEACON) {
            for (String resName : resData.getKeys(false)) {
                if (!resData.isSet(resName + ".location")) continue;
                if (Util.stringToLocation(resData.getString(resName + ".location"))
                        .distance(event.getBlock().getLocation()) >= 1) continue;
                event.setCancelled(true);
                int count = resData.getInt(resName + ".break");
                count++;
                Player player = event.getPlayer();
                if (count >= 20) {
                    Location loc = event.getBlock().getLocation();
                    loc.getBlock().setType(Material.AIR);
                    loc.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
                    loc.clone().add(1, -1, 0).getBlock().setType(Material.AIR);
                    loc.clone().add(1, -1, 1).getBlock().setType(Material.AIR);
                    loc.clone().add(1, -1, -1).getBlock().setType(Material.AIR);
                    loc.clone().add(-1, -1, 0).getBlock().setType(Material.AIR);
                    loc.clone().add(-1, -1, 1).getBlock().setType(Material.AIR);
                    loc.clone().add(-1, -1, -1).getBlock().setType(Material.AIR);
                    loc.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
                    loc.clone().add(0, -1, 1).getBlock().setType(Material.AIR);
                    loc.clone().add(0, -1, -1).getBlock().setType(Material.AIR);
                    ResidenceApi.getResidenceManager().getByName(resName).remove();
                    String playerName = resData.getString(resName + ".player");
                    double cost = resData.getDouble(resName + ".cost");
                    AdvRes.econ.depositPlayer(Bukkit.getOfflinePlayer(playerName), cost);
                    resData.set(resName, null);
                    player.sendMessage(S.toPrefixRed("信标已被破坏 20 次，领地已删除"));
                } else {
                    resData.set(resName + ".break", count);
                    player.sendMessage(S.toPrefixRed("信标已被破坏 " + count + " 次"));
                }
                resData.save();
            }
        } else {
            if (resData.getKeys(false).stream()
                    .filter(s -> resData.isSet(s + ".location"))
                    .map(s -> Util.stringToLocation(resData.getString(s + ".location")))
                    .filter(beacon -> {
                        Location loc = event.getBlock().getLocation();
                        int centerX = beacon.getBlockX();
                        int centerZ = beacon.getBlockZ();
                        int blockX = loc.getBlockX();
                        int blockZ = loc.getBlockZ();
                        return Math.abs(blockX - centerX) < 3 && Math.abs(blockZ - centerZ) < 3;
                    }).count() > 0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlaced(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (resData.getKeys(false).stream()
                .filter(s -> resData.isSet(s + ".location"))
                .map(s -> Util.stringToLocation(resData.getString(s + ".location")))
                .filter(beacon -> {
                    Location loc = event.getBlock().getLocation();
                    int centerX = beacon.getBlockX();
                    int centerZ = beacon.getBlockZ();
                    int blockX = loc.getBlockX();
                    int blockZ = loc.getBlockZ();
                    return Math.abs(blockX - centerX) < 3 && Math.abs(blockZ - centerZ) < 3;
                }).count() > 0) {
//            event.setCancelled(true);
        }
    }
}