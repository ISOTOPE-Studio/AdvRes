package cc.isotopestudio.advres;

import cc.isotopestudio.advres.listener.ResListener;
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

    private static final String pluginName = "AdvRes";
    public static final String prefix = (new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("[")
            .append("AdvRes").append("]").append(ChatColor.RED).toString();

    public static AdvRes plugin;

    public static PluginFile config;
    public static PluginFile resData;

    public static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        config = new PluginFile(this, "config.yml", "config.yml");
        config.setEditable(false);
        resData = new PluginFile(this, "res.yml");

        //this.getCommand("csclass").setExecutor(new CommandCsclass());
        ResidenceInterface resMan = ResidenceApi.getResidenceManager();

        getLogger().info("加载Vault API");
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - 无法载入，原因：Vault未安装",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ResListener(), this);
        new PlacementTimeOutTask().runTaskTimer(this, 20, 100);

        getLogger().info(pluginName + "成功加载!");
        getLogger().info(pluginName + "由ISOTOPE Studio制作!");
        getLogger().info("http://isotopestudio.cc");
    }

    public void onReload() {
    }

    @Override
    public void onDisable() {
        getLogger().info(pluginName + "成功卸载!");
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
