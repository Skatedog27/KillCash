package me.Skatedog27.KillCash;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KCListener extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("KCListener has been enabled!");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new KillCash(), this);
    }
 
    @Override
    public void onDisable() {
        getLogger().info("KCListener has been disabled!");
    }
}
