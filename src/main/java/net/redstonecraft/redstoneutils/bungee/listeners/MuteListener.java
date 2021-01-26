package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.MuteManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.Mute;

public class MuteListener implements Listener {

    private final MuteManager muteManager;

    public MuteListener(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChatEvent event) {
        if (!event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (muteManager.isMuted(UserManager.fromProxiedPlayer(player))) {
                event.setCancelled(true);
                Mute mute = muteManager.getLatestMute(UserManager.fromProxiedPlayer(player));
                player.sendMessage(Main.prefix + Main.renderColors("&cDu bist noch " + Main.renderTimeDiff(mute.until - Main.getCurrentTime()) + " gemuted wegen &6" + mute.reason + "&r&c von &6" + (mute.mutedByConsole ? "CONSOLE" : mute.mutedBy.name) + "&c."));
            }
        }
    }

}
