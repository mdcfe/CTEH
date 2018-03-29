package io.github.md678685.minecraft.cteh;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import net.citizensnpcs.api.npc.NPC;
import net.ess3.api.IEssentials;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * CTEH: Citizens2-Essentials Helper
 *
 * @author MD678685
 * @version 0.1
 */
@Plugin(
        name = "CTEH",
        version = "${full.version}",
        description = "A plugin to ease managing Essentials user data for Citizens2 NPCs.",
        authors = {"MD678685"},
        website = "https://github.com/md678685/CTEH/",
        hardDepends = {"Essentials", "Citizens"}
)
public class CTEH extends ExtendedJavaPlugin {
    private static IEssentials ess = Essentials.getPlugin(Essentials.class);

    static boolean tagNpcs = true;
    static boolean autoDelete = true;
    private static boolean isDebug = false;

    @Override
    public void enable() {
        this.loadConfig("config.yml");

        tagNpcs = this.getConfig().getBoolean("tagNpcs", true);
        autoDelete = this.getConfig().getBoolean("autoDelete", true);
        isDebug = this.getConfig().getBoolean("debug", false);

        CTEHCommands.register(this);
        CTEHEvents.register(this);
    }

    public User getUserForNpc(NPC npc) {
        User user;
        if (npc.isSpawned()) {
            user = ess.getUser(npc.getEntity());
            if (user != null) return user;
        }
        debugLog("Unable to get user by entity; trying UUID...");
        return ess.getUser(npc.getUniqueId());
    }

    public void tagNpc(NPC npc) {
        User user = getUserForNpc(npc);
        if (user == null) {
            debugLog("tagNpc: User for NPC missing:",
                    "ID:   " + npc.getId(),
                    "Name: " + npc.getFullName(),
                    "UUID: " + npc.getUniqueId().toString());
            return;
        }
        user.setConfigProperty("citizens", npc.getId());
        debugLog("Updated Ess config for NPC " + npc.getUniqueId().toString());
    }

    public void deleteNpc(NPC npc) {
        User user = getUserForNpc(npc);
        if (user == null) {
            debugLog("deleteNpc: User for NPC missing:",
                    "ID:   " + npc.getId(),
                    "Name: " + npc.getFullName(),
                    "UUID: " + npc.getUniqueId().toString());
            return;
        }
        user.reset();
        debugLog("Deleted Ess config for NPC " + npc.getUniqueId().toString());
    }

    boolean toggleDebug() {
        isDebug = !isDebug;
        return isDebug;
    }

    void inform(CommandSender sender, String msg) {
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

    void debugLog(String ...msgs) {
        if (isDebug) {
            for (String msg : msgs) {
                this.getLogger().info("DEBUG: " + msg);
            }
        }
    }
}
