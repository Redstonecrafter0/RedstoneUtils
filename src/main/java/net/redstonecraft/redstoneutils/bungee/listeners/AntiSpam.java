package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneapi.tools.StringUtils;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.ChatManager;

public class AntiSpam implements Listener {

    private final ChatManager chatManager;

    public AntiSpam(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (!player.hasPermission("redstoneutils.chat.bypass")) {
                if (chatManager.isLimited(player)) {
                    event.setCancelled(true);
                    ((ProxiedPlayer) event.getSender()).sendMessage(Main.prefix + Main.renderColors("&cSchreibe etwas langsamer."));
                } else if (!StringUtils.isValid(event.getMessage(), StringUtils.DEFAULT_WHITELISTED_CHARS)) {
                    event.setCancelled(true);
                    ((ProxiedPlayer) event.getSender()).sendMessage(Main.prefix + Main.renderColors("&cDeine Nachricht enthält ungültige Zeichen."));
                }
                chatManager.limit(player);
            }
        }
    }

}
