package net.redstonecraft.redstoneutils.bungee.listeners;

import net.labymod.serverapi.bungee.event.LabyModPlayerJoinEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;

public class LabyModListener implements Listener {

    private final LabyModManager labyModManager;

    public LabyModListener(LabyModManager labyModManager) {
        this.labyModManager = labyModManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(LabyModPlayerJoinEvent event) {
        labyModManager.addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerDisconnectEvent event) {
        labyModManager.removePlayer(event.getPlayer());
    }

}
