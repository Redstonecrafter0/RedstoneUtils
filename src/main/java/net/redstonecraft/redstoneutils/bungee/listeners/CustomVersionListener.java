package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CustomVersionListener implements Listener {

    private final String version;
    private final Integer protocol;

    public CustomVersionListener(String version, boolean showAlways) {
        this.version = version;
        this.protocol = showAlways ? 0 : null;
    }

    @EventHandler
    public void onSLP(ProxyPingEvent event) {
        ServerPing slp = event.getResponse();
        if (protocol == null) {
            slp.setVersion(new ServerPing.Protocol(version, slp.getVersion().getProtocol()));
        } else {
            slp.setVersion(new ServerPing.Protocol(version, protocol));
        }
    }

}
