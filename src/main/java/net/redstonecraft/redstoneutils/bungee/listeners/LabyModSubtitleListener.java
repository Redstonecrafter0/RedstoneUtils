package net.redstonecraft.redstoneutils.bungee.listeners;

import net.labymod.serverapi.bungee.event.MessageReceiveEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneapi.tools.StringUtils;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.LabyModSubtitle;
import net.redstonecraft.redstoneutils.enums.LabyModPromts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LabyModSubtitleListener implements Listener {

    private final LabyModManager labyModManager;
    private final Main plugin;
    private List<ProxiedPlayer> slowdown = new ArrayList<>();

    public LabyModSubtitleListener(LabyModManager labyModManager, Main plugin) {
        this.labyModManager = labyModManager;
        this.plugin = plugin;
        ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> slowdown = new ArrayList<>(), 0, 5, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            if (labyModManager.isLabyModPlayer(event.getPlayer())) {
                List<LabyModSubtitle> list = new ArrayList<>();
                String subtitle = labyModManager.getSubtitle(UserManager.fromProxiedPlayer(event.getPlayer()));
                for (ProxiedPlayer i : event.getServer().getInfo().getPlayers()) {
                    if (labyModManager.isLabyModPlayer(i)) {
                        LabyModManager.setSubtitles(i, Collections.singletonList(new LabyModSubtitle(event.getPlayer().getUniqueId(), subtitle)));
                        list.add(new LabyModSubtitle(i.getUniqueId(), labyModManager.getSubtitle(UserManager.fromProxiedPlayer(event.getPlayer()))));
                    }
                }
                LabyModManager.setSubtitles(event.getPlayer(), list);
            }
        }, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onLMCReceive(MessageReceiveEvent event) {
        if (event.getMessageKey().equals("input_prompt")) {
            if (event.getJsonElement().getAsJsonObject().get("id").getAsInt() == LabyModPromts.SUBTITLE.id) {
                if (!slowdown.contains(event.getPlayer())) {
                    slowdown.add(event.getPlayer());
                    String value = event.getJsonElement().getAsJsonObject().get("value").getAsString();
                    if (value.length() > 20 || !StringUtils.isValid(value, StringUtils.DEFAULT_WHITELISTED_CHARS)) {
                        event.getPlayer().sendMessage(Main.prefix + Main.renderColors("&cUngültige Eingabe."));
                        return;
                    }
                    if (value.equals("")) {
                        value = null;
                    }
                    labyModManager.setSubtitle(UserManager.fromProxiedPlayer(event.getPlayer()), value);
                    for (ProxiedPlayer i : event.getPlayer().getServer().getInfo().getPlayers()) {
                        LabyModManager.setSubtitles(i, Collections.singletonList(new LabyModSubtitle(event.getPlayer().getUniqueId(), value)));
                    }
                    event.getPlayer().sendMessage(Main.prefix + Main.renderColors("&aSubtitle erfolgreich geupdated."));
                } else {
                    event.getPlayer().sendMessage(Main.prefix + Main.renderColors("&cWarte bevor du deinen Subtitle updatest."));
                }
            }
        }
    }

}
