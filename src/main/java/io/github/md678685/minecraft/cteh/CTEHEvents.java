package io.github.md678685.minecraft.cteh;

import me.lucko.helper.Events;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.event.EventPriority;

public class CTEHEvents {
    static void register(final CTEH plugin) {
        Events.merge(NPCEvent.class, EventPriority.MONITOR, NPCCreateEvent.class, NPCSpawnEvent.class)
                .filter(ignored -> CTEH.tagNpcs)
                .handler(e -> plugin.tagNpc(e.getNPC()));

        Events.subscribe(NPCRemoveEvent.class, EventPriority.MONITOR)
                .filter(ignored -> CTEH.autoDelete)
                .handler(e -> plugin.deleteNpc(e.getNPC()));

    }
}
