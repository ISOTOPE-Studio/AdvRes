package cc.isotopestudio.advres.command;
/*
 * Created by Mars Tan on 8/3/2017.
 * Copyright ISOTOPE Studio
 */

import cc.isotopestudio.advres.util.S;
import cc.isotopestudio.advres.util.Util;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static cc.isotopestudio.advres.AdvRes.plugin;
import static cc.isotopestudio.advres.AdvRes.resData;
import static cc.isotopestudio.advres.task.ConfigLoadTask.PLAYERBROKEMAP;

public class AdvresCommand implements CommandExecutor {

    private static final Map<Player, String> requestMap = new HashMap<>();

    private static final Set<Material> MATERIALS = new HashSet<>();

    public AdvresCommand() {
        MATERIALS.add(Material.AIR);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("advres")) {
            if (args.length < 1) {
                sender.sendMessage(S.toPrefixGreen("�����˵�"));
                sender.sendMessage(S.toYellow("/" + label + " block <�����> - ����ϵ��ű��������"));
                sender.sendMessage(S.toYellow("/" + label + " rank - ����ϵ��ű��������"));
                return true;
            }
            if (args[0].equals("rank")) {
                if (PLAYERBROKEMAP.size() > 0) {
                    sender.sendMessage(S.toPrefixGreen("�ƻ�����ű����а�"));
                    List<Map.Entry<String, Integer>> infoIds = new ArrayList<>(PLAYERBROKEMAP.entrySet());
                    infoIds.sort(Comparator.comparing(o -> (o.getValue())));
                    int count = 0;
                    for (int i = infoIds.size() - 1; i >= 0; i--) {
                        count++;
                        if (count > 5) break;
                        sender.sendMessage(S.toYellow(" - " + infoIds.get(i).getKey()));
                    }
                } else {
                    sender.sendMessage(S.toPrefixRed("û�м�¼"));
                }
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(S.toPrefixRed("���ִ�е�����"));
                return true;
            }
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("block")) {
                if (args.length < 2) {
                    sender.sendMessage(S.toYellow("/" + label + " block <�����> - ����ϵ��ű��������"));
                    return true;
                }
                Player invite = Bukkit.getPlayer(args[1]);
                if (invite == null) {
                    player.sendMessage(S.toPrefixRed("��Ҳ�����"));
                    return true;
                }
                Block targetBlock = player.getTargetBlock(MATERIALS, 5);
                if (targetBlock == null || targetBlock.getType() != Material.BEACON) {
                    player.sendMessage(S.toPrefixRed("�ⲻ������ű�"));
                    return true;
                }
                for (String resName : resData.getKeys(false)) {
                    if (!resData.isSet(resName + ".location")) continue;
                    if (Util.stringToLocation(resData.getString(resName + ".location"))
                            .distance(targetBlock.getLocation()) >= 1) continue;
                    String ownerName = resData.getString(resName + ".player");
                    if (!ownerName.equals(player.getName())) {
                        player.sendMessage(S.toPrefixRed("�ⲻ���������ű�"));
                        return true;
                    }
                    FancyMessage msg = new FancyMessage(
                            S.toPrefixYellow(player.getDisplayName() + " ϣ�������� " + resName + " ������˺�  "));
                    msg.then("[ͬ��] ").style(ChatColor.BOLD).color(ChatColor.GREEN).command("/advres accept")
                            .then("[�ܾ�]").style(ChatColor.BOLD).color(ChatColor.RED).command("/advres decline");
                    msg.send(invite);
                    requestMap.put(invite, resName);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            requestMap.remove(invite, resName);
                        }
                    }.runTaskLater(plugin, 20 * 15);
                    sender.sendMessage(S.toPrefixGreen("�ɹ�����"));
                    return true;
                }
                player.sendMessage(S.toPrefixRed("�ⲻ������ű�"));
                return true;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                if (requestMap.containsKey(player)) {
                    String resName = requestMap.remove(player);
                    List<String> blocked = resData.getStringList(resName + ".blocked");
                    blocked.add(player.getName());
                    resData.set(resName + ".blocked", blocked);
                    resData.save();
                    String ownerName = resData.getString(resName + ".player");
                    Player owner = Bukkit.getPlayerExact(ownerName);
                    if (owner != null) {
                        owner.sendMessage(S.toPrefixGreen(player.getDisplayName() + " ����������"));
                    }
                    sender.sendMessage(S.toPrefixGreen("�ɹ���������"));
                } else {
                    player.sendMessage(S.toPrefixRed("��û���յ�����/�������ѹ���"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("decline")) {
                if (requestMap.containsKey(player)) {
                    String resName = requestMap.remove(player);
                    String ownerName = resData.getString(resName + ".player");
                    Player owner = Bukkit.getPlayerExact(ownerName);
                    if (owner != null) {
                        owner.sendMessage(S.toPrefixRed(player.getDisplayName() + " �ܾ�������"));
                    }
                    sender.sendMessage(S.toPrefixGreen("�ɹ��ܾ�����"));
                } else {
                    player.sendMessage(S.toPrefixRed("��û���յ�����/�������ѹ���"));
                }
                return true;
            }
            sender.sendMessage(S.toYellow("/" + label + " block <�����> - ����ϵ��ű��������"));
            return true;
        }
        return false;
    }
}
