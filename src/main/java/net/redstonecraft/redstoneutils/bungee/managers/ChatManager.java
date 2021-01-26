package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatManager {

    private final List<ProxiedPlayer> limited = new ArrayList<>();
    private final Main plugin;

    public ChatManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean isLimited(ProxiedPlayer player) {
        return limited.contains(player);
    }

    public void limit(ProxiedPlayer player) {
        if (!isLimited(player)) {
            limited.add(player);
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> limited.remove(player), plugin.getConfig().getLong("chatcooldown.duration"), TimeUnit.SECONDS);
        }
    }

}
