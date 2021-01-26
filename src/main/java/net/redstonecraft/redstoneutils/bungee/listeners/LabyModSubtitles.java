package net.redstonecraft.redstoneutils.bungee.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.LabyModPlayerSubtitle;

import java.util.ArrayList;
import java.util.List;

public class LabyModSubtitles implements Listener {

    private final LabyModManager labyModManager;

    public LabyModSubtitles(LabyModManager labyModManager) {
        this.labyModManager = labyModManager;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        if (labyModManager.isLabyModPlayer(event.getPlayer())) {
            List<LabyModPlayerSubtitle> list = new ArrayList<>();
            for (ProxiedPlayer i : event.getServer().getInfo().getPlayers()) {
                if (labyModManager.isLabyModPlayer(i)) {
                    list.add(new LabyModPlayerSubtitle(i.getUniqueId(), labyModManager.getSubtitle(UserManager.fromProxiedPlayer(i))));
                }
            }
            setSubtitle(event.getPlayer(), list);
        }
    }

    public static void setSubtitle(ProxiedPlayer receiver, Iterable<LabyModPlayerSubtitle> subtitles) {
        JsonArray array = new JsonArray();
        for (LabyModPlayerSubtitle i : subtitles) {
            JsonObject subtitle = new JsonObject();
            subtitle.addProperty("uuid", i.uuid.toString());
            subtitle.addProperty("size", 1.2d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)
            if (i.subtitle != null) {
                subtitle.addProperty("value", i.subtitle);
            }
            array.add(subtitle);
        }
        LabyModPlugin.getInstance().sendServerMessage(receiver, "account_subtitle", array);
    }

}

