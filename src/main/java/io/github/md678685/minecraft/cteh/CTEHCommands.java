package io.github.md678685.minecraft.cteh;

import me.lucko.helper.Commands;

public class CTEHCommands {
    static void register(final CTEH plugin) {
        Commands.create()
                .assertPermission("cteh.debug", "You can't do that!")
                .handler(c -> {
                    boolean debugState = plugin.toggleDebug();
                    plugin.inform(c.sender(), "Debug mode " + (debugState ? "enabled" : "disabled"));
                })
                .registerAndBind(plugin, plugin, "ctehdebug");
    }
}
