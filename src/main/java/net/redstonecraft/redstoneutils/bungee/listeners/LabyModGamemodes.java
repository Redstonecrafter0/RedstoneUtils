package net.redstonecraft.redstoneutils.bungee.listeners;

import com.google.gson.JsonObject;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;

public class LabyModGamemodes implements Listener {

    private final LabyModManager labyModManager;

    public LabyModGamemodes(LabyModManager labyModManager) {
        this.labyModManager = labyModManager;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        if (labyModManager.isLabyModPlayer(event.getPlayer())) {
            sendCurrentPlayingGamemode(event.getPlayer(), event.getServer().getInfo().getMotd());
        }
    }

    private static void sendCurrentPlayingGamemode(ProxiedPlayer player, String gamemodeName) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", true);
        object.addProperty("gamemode_name", gamemodeName);
        LabyModPlugin.getInstance().sendServerMessage(player, "server_gamemode", object);
    }

}
