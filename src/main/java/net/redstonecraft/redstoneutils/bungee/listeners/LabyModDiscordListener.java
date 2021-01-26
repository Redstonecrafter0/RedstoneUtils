package net.redstonecraft.redstoneutils.bungee.listeners;

import com.google.gson.JsonObject;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.labymod.serverapi.bungee.event.MessageReceiveEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LabyModDiscordListener implements Listener {

    private final String domain;
    private final Main plugin;
    private final LabyModManager labyModManager;
    private List<String> onCooldown = new ArrayList<>();
    private final boolean cloudActive = ProxyServer.getInstance().getPluginManager().getPlugin("RedstoneCloud") != null;

    public LabyModDiscordListener(Main plugin, String domain, LabyModManager labyModManager) {
        this.domain = domain;
        this.labyModManager = labyModManager;
        this.plugin = plugin;
        ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> onCooldown = new ArrayList<>(), 0, 30, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onLMCReceive(MessageReceiveEvent event) {
        try {
            if (event.getMessageKey().equals("INFO") && !cloudActive) {
                JsonObject obj = new JsonObject();
                addSecret(obj, "hasMatchSecret", "matchSecret", null, domain);
                addSecret(obj, "hasSpectateSecret", "spectateSecret", null, domain);
                addSecret(obj, "hasJoinSecret", "joinSecret", null, domain);
                LabyModPlugin.getInstance().sendServerMessage(event.getPlayer(), "discord_rpc", obj);
            }
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onServerChanged(ServerConnectedEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            if (labyModManager.isLabyModPlayer(event.getPlayer())) {
                updateGameInfo(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void updateGameInfo(ProxiedPlayer player, String gamemode) {
        JsonObject obj = new JsonObject();
        obj.addProperty("hasGame", true);
        obj.addProperty("game_mode", gamemode);
        obj.addProperty("game_startTime", 0);
        obj.addProperty("game_endTime", 0);
        LabyModPlugin.getInstance().sendServerMessage(player, "discord_rpc", obj);
    }

    private static void addSecret(JsonObject jsonObject, String hasKey, String key, UUID secret, String domain) {
        jsonObject.addProperty(hasKey, true);
        jsonObject.addProperty(key, secret.toString() + ":" + domain);
    }

}
