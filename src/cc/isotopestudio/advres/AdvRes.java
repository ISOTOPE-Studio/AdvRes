package cc.isotopestudio.advres;

import cc.isotopestudio.advres.command.AdvresCommand;
import cc.isotopestudio.advres.listener.ResListener;
import cc.isotopestudio.advres.task.AnnouncementTask;
import cc.isotopestudio.advres.task.BeaconHealTask;
import cc.isotopestudio.advres.task.ConfigLoadTask;
import cc.isotopestudio.advres.task.PlacementTimeOutTask;
import cc.isotopestudio.advres.util.PluginFile;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvRes extends JavaPlugin {

    private static final String pluginName = "���";
    public static final String prefix = (new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("[")
            .append("AdvRes").append("]").append(ChatColor.RED).toString();

    public static AdvRes plugin;

    public static PluginFile config;
    public static PluginFile resData;
    public static PluginFile msgData;
    public static PluginFile playerData;

    public static int BEACONBREAKCOUNT;
    public static String ANNOUNCEMENTMSG;

    public static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        config = new PluginFile(this, "config.yml", "config.yml");
        config.setEditable(false);
        resData = new PluginFile(this, "res.yml");
        msgData = new PluginFile(this, "msg.yml");
        playerData = new PluginFile(this, "player.yml");

        new ConfigLoadTask().runTask(this);

        this.getCommand("advres").setExecutor(new AdvresCommand());
        ResidenceInterface resMan = ResidenceApi.getResidenceManager();

        getLogger().info("����Vault API");
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - �޷����룬ԭ��Vaultδ��װ",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ResListener(), this);
        new PlacementTimeOutTask().runTaskTimer(this, 20, 100);
        new BeaconHealTask().runTaskTimer(this, 20, 20 * 60 * 10);
        new AnnouncementTask().runTaskTimer(this, 10 * 20, 20 * 60 * 180);

        getLogger().info(pluginName + "�ɹ�����!");
        getLogger().info(pluginName + "��ISOTOPE Studio����!");
        getLogger().info("http://isotopestudio.cc");
    }

    public void onReload() {
        config.reload();
        resData.reload();
        msgData.reload();
        playerData.reload();

        new ConfigLoadTask().runTask(this);
    }

    @Override
    public void onDisable() {
        getLogger().info(pluginName + "�ɹ�ж��!");
    }

    // Vault API
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
}
