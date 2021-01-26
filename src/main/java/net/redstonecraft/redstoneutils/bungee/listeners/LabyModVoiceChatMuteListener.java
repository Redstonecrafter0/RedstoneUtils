package net.redstonecraft.redstoneutils.bungee.listeners;

import com.google.gson.JsonObject;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.labymod.serverapi.bungee.event.LabyModPlayerJoinEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.events.MuteEvent;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;
import net.redstonecraft.redstoneutils.bungee.managers.MuteManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LabyModVoiceChatMuteListener implements Listener {

    private final Main plugin;
    private final MuteManager muteManager;
    private final LabyModManager labyModManager;
    private final List<ProxiedPlayer> mutedOnline = new ArrayList<>();

    public LabyModVoiceChatMuteListener(MuteManager muteManager, LabyModManager labyModManager, Main plugin) {
        this.muteManager = muteManager;
        this.labyModManager = labyModManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(LabyModPlayerJoinEvent event) {
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        mutedOnline.remove(event.getPlayer());
    }

    @EventHandler
    public void onMute(MuteEvent event) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(event.mute.user.uuid);
        if (player != null) {
            for (ProxiedPlayer i : player.getServer().getInfo().getPlayers()) {
                if (labyModManager.isLabyModPlayer(i)) {
                    sendMutedPlayerTo(i, player.getUniqueId(), true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSubJoin(ServerConnectedEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            if (mutedOnline.contains(event.getPlayer())) {
                for (ProxiedPlayer i : event.getServer().getInfo().getPlayers()) {
                    if (labyModManager.isLabyModPlayer(i)) {
                        sendMutedPlayerTo(i, event.getPlayer().getUniqueId(), true);
                    }
                }
            }
            for (ProxiedPlayer i : event.getServer().getInfo().getPlayers()) {
                if (labyModManager.isLabyModPlayer(i)) {
                    if (muteManager.isMuted(UserManager.fromProxiedPlayer(i))) {
                        sendMutedPlayerTo(event.getPlayer(), i.getUniqueId(), true);
                    }
                }
            }
            if (muteManager.isMuted(UserManager.fromProxiedPlayer(event.getPlayer()))) {
                disableVoiceChat(event.getPlayer());
                mutedOnline.add(event.getPlayer());
            }
        }, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onSubLeave(ServerDisconnectEvent event) {
        if (mutedOnline.contains(event.getPlayer())) {
            for (ProxiedPlayer i : event.getTarget().getPlayers()) {
                if (labyModManager.isLabyModPlayer(i) && !i.equals(event.getPlayer())) {
                    sendMutedPlayerTo(i, event.getPlayer().getUniqueId(), false);
                }
            }
        }
        for (ProxiedPlayer i : event.getTarget().getPlayers()) {
            if (labyModManager.isLabyModPlayer(i)) {
                if (muteManager.isMuted(UserManager.fromProxiedPlayer(i))) {
                    sendMutedPlayerTo(event.getPlayer(), i.getUniqueId(), false);
                }
            }
        }
    }

    private static void sendSettings(ProxiedPlayer player) {
        JsonObject voicechatObject = new JsonObject();
        voicechatObject.addProperty("keep_settings_on_server_switch", true);
        JsonObject requestSettingsObject = new JsonObject();
        requestSettingsObject.addProperty("required", false);
        JsonObject settingsObject = new JsonObject();
        settingsObject.addProperty("enabled", true); // Force player to enable the VoiceChat
        settingsObject.addProperty("microphoneVolume", 10); // Own microphone volume. (0 - 10, Default 10)
        settingsObject.addProperty("surroundRange", 10); // Range of the players you can hear (5 - 18, Default: 10)
        settingsObject.addProperty("surroundVolume", 10); // Volume of other players (0 - 10, Default: 10)
        settingsObject.addProperty("continuousTransmission", false); // Speak without push to talk
        requestSettingsObject.add("settings", settingsObject);
        voicechatObject.add("request_settings", requestSettingsObject);
        LabyModPlugin.getInstance().sendServerMessage(player, "voicechat", voicechatObject);
    }

    private static void sendMutedPlayerTo(ProxiedPlayer player, UUID mutedPlayer, boolean muted) {
        JsonObject voicechatObject = new JsonObject();
        JsonObject mutePlayerObject = new JsonObject();
        mutePlayerObject.addProperty("mute", muted);
        mutePlayerObject.addProperty("target", mutedPlayer.toString());
        voicechatObject.add("mute_player", mutePlayerObject);
        LabyModPlugin.getInstance().sendServerMessage(player, "voicechat", voicechatObject);
    }

    private static void disableVoiceChat(ProxiedPlayer player) {
        JsonObject object = new JsonObject();
        object.addProperty("allowed", false);
        LabyModPlugin.getInstance().sendServerMessage(player, "voicechat", object);
    }

    private static void anableVoiceChat(ProxiedPlayer player) {
        JsonObject object = new JsonObject();
        object.addProperty("allowed", true);
        LabyModPlugin.getInstance().sendServerMessage(player, "voicechat", object);
    }

}
