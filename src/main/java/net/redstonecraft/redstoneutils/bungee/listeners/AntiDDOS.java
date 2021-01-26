package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.Main;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AntiDDOS implements Listener {

    private List<String> blacklisted = new ArrayList<>();
    private final Favicon favicon;

    public AntiDDOS(Main plugin) {
        Favicon favicon1;
        try {
            favicon1 = Favicon.create(ImageIO.read(new File("favicon.png")));
        } catch (IOException ignored) {
            favicon1 = null;
        }
        favicon = favicon1;
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> blacklisted = new ArrayList<>(), 0, 1, TimeUnit.HOURS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSLP(ProxyPingEvent event) {
        if (blacklisted.contains(event.getConnection().getAddress().getHostName())) {
            ServerPing slp = event.getResponse();
            if (favicon != null) {
                slp.setFavicon(favicon);
            }
            event.setResponse(slp);
        } else {
            blacklisted.add(event.getConnection().getAddress().getHostName());
        }
    }

}
