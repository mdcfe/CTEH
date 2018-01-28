package io.github.md678685.minecraft.cteh;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import net.ess3.api.IEssentials;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CTEH: Citizens2-Essentials Helper
 *
 * @author MD678685
 * @version 0.1
 */
public class CTEH extends JavaPlugin implements Listener, CommandExecutor {
    private static IEssentials ess = Essentials.getPlugin(Essentials.class);

    private static boolean tagNpcs = true;
    private static boolean autoDelete = true;
    private static boolean isDebug = false;

    @Override
    public void onEnable() {
        this.reloadConfig();

        tagNpcs = this.getConfig().getBoolean("tagNpcs", true);
        autoDelete = this.getConfig().getBoolean("autoDelete", true);
        isDebug = this.getConfig().getBoolean("debug", false);
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNPCEvent(NPCEvent event) {
        NPC npc = event.getNPC();

        User user = ess.getUser(npc.getUniqueId());
        if (user == null) {
            debugLog("(general) User for NPC missing:",
                    "ID:   " + npc.getId(),
                    "Name: " + npc.getFullName(),
                    "UUID: " + npc.getUniqueId().toString());
            return;
        }
        user.setConfigProperty("citizens", npc.getId());
        debugLog("Updated Ess config for NPC " + npc.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCRemoved(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
        User user = ess.getUser(npc.getUniqueId());
        if (user == null) {
            debugLog("(remove) User for NPC missing:",
                    "ID:   " + npc.getId(),
                    "Name: " + npc.getFullName(),
                    "UUID: " + npc.getUniqueId().toString());
            return;
        }
        user.reset();
        debugLog("Deleted Ess config for NPC " + npc.getUniqueId().toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) return false;
        switch (args[0]) {
            case "debug":
                if (!sender.hasPermission("cteh.debug")) {
                    this.inform(sender, "You don't have permission to toggle debug mode.");
                    return true;
                }

                isDebug = !isDebug;
                this.inform(sender, "Debug mode " + (isDebug ? "enabled" : "disabled") + ".");
                return true;

            default:
                return false;
        }
    }

    private void inform(CommandSender sender, String msg) {
        BaseComponent[] components = new ComponentBuilder("[CTEH] ")
                .color(ChatColor.GREEN)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Citizens2 Essentials Helper").create()))
                .append(msg)
                .color(ChatColor.WHITE)
                .create();
        sender.spigot().sendMessage(components);

        if (sender instanceof ConsoleCommandSender) return;
        this.getLogger().info(sender.getName() + ": " + msg);
    }

    private void debugLog(String ...msgs) {
        if (isDebug) {
            for (String msg : msgs) {
                this.getLogger().info("DEBUG: " + msg);
            }
        }
    }
}
