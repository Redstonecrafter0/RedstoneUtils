package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.HubManager;

public class HubListener implements Listener {

    private final HubManager hubManager;

    public HubListener(HubManager hubManager) {
        this.hubManager = hubManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        if (hubManager.countLobbiesOnline() > 0 && !hubManager.contains(event.getKickedFrom())) {
            ServerInfo server = hubManager.getRandomLobby(event.getKickedFrom());
            if (server != null) {
                event.setCancelled(true);
                event.setCancelServer(server);
                event.getPlayer().sendMessage(Main.prefix + Main.renderColors("&cConnecting to Lobby..."));
                event.getPlayer().sendMessage(event.getKickReasonComponent());
            }
        }
    }

}
