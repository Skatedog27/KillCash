package me.Skatedog27.KillCash;
 
import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
 
public class KillCash extends JavaPlugin implements Listener {
	
	FileConfiguration config;
	File cfile;
       
        public static Economy econ = null;
       
    public void onEnable() {
        Bukkit.getServer().getLogger().info("KillCash has been enabled!");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);;
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		cfile = new File(getDataFolder(), "config.yml");
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
    	Player p = e.getPlayer();
    	
    	if (!p.hasPermission("kc.sign.info")) {
    		p.sendMessage(ChatColor.DARK_RED + "You cannot use this function!");
    		return;
    	}
    	if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if (e.getClickedBlock().getState() instanceof Sign) {
    			Sign s = (Sign) e.getClickedBlock().getState();
    			if (s.getLine(0).equalsIgnoreCase("[KillCashInfo]")) {
    				p.sendMessage(ChatColor.GOLD + "Kill mobs and players for cash towards items in economy!");
    				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
    				return;
    			}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public boolean onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Monster) {
            Monster monsterEnt = (Monster) event.getEntity();
            Player player = monsterEnt.getKiller();
           
            if(((Permissible)player).hasPermission("kc.cash.mob")) {
            	player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 10, 1);
            	player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 1);
            	player.playEffect(player.getLocation(), Effect.POTION_BREAK, 5);
                EconomyResponse r = econ.depositPlayer(player.getName(), 5.00);
                if(r.transactionSuccess()) {
                        ((CommandSender) player).sendMessage(ChatColor.GOLD + "You have had " + ChatColor.GREEN + "5.00$" + ChatColor.GOLD + " added to your bank from killing a " + ChatColor.RED + event.getEntity());
                        return true;
                }
                return true;
            }
            return true;
        }
        return true;
    }
	@SuppressWarnings("deprecation")
	@EventHandler
    public boolean onPlayerDeath(PlayerDeathEvent event) {
    	if(event.getEntity() instanceof Player) {
    		Player mcplayer = (Player) event.getEntity();
    		Player player = mcplayer.getKiller();
    		
    		if(((Permissible)player).hasPermission("kc.cash.player")) {
    			player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
    			player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1, 1);
    			player.playEffect(player.getLocation(), Effect.POTION_BREAK, 5);
                EconomyResponse r = econ.depositPlayer(player.getName(), 10.00);
                if(r.transactionSuccess()) {
                        ((CommandSender) player).sendMessage(ChatColor.GOLD + "You have had " + ChatColor.GREEN + "10.00$" + ChatColor.GOLD + " added to your bank from killing a " + ChatColor.RED + event.getEntity().getName());
                        return true;
                }
                return true;
    		}
    		return true;
    	}
    	return true;
    }
   
        private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        	Player player = (Player) sender;
        	
                if (cmd.getName().equalsIgnoreCase("kc")) {
                        if (!sender.hasPermission("kc.command")) {
                                sender.sendMessage(ChatColor.DARK_RED + "You do not have Permission!");
                                return true;
                        }
                        else if (args.length == 1) {
                                if (args[0].equalsIgnoreCase("info")) {
                                        if (!sender.hasPermission("kc.command.info")) {
                                                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                                return true;
                                        }
                                        sender.sendMessage(ChatColor.DARK_AQUA + "KillCash - by Skatedog27 - Version 2.1" + ChatColor.RED + "REQUIRES VAULT + AN ECONOMY PLUGIN");
                                        return true;
                                }
                                else if (args[0].equalsIgnoreCase("money")) {
                                	if (!sender.hasPermission("kc.command.money")) {
                                		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                		return true;
                                	}
                                	sender.sendMessage(ChatColor.DARK_GRAY + "Your current balance is: " + ChatColor.GOLD + econ.getBalance(player.getName()));
                                	player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                                }
                                else if (args[0].equalsIgnoreCase("reload")) {
                                	if (!sender.hasPermission("kc.command.reload")) {
                                		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                		return true;
                                	}
                                	config = YamlConfiguration.loadConfiguration(cfile);
                                	sender.sendMessage(ChatColor.DARK_GRAY + "Configuration file has been reloaded " + ChatColor.GOLD + "Successfully!");
                                }
                                else if (args[0].equalsIgnoreCase("configcheck")) {
                                	if (!sender.hasPermission("kc.command.configcheck")) {
                                		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                		return true;
                                	}
                                	sender.sendMessage(ChatColor.DARK_GRAY + "Config test message: " + ChatColor.GOLD + config.getString("test"));
                                	return true;
                                }
                                return true;
                        }
                        sender.sendMessage(ChatColor.RED + "Usage: /kc (info; money; reload; configcheck)");
                        return true;
                }
                return true;
        }
}
