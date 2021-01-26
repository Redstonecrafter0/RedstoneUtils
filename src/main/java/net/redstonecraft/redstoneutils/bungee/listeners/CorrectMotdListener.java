package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class CorrectMotdListener implements Listener {

    private final String motd;

    public CorrectMotdListener() {
        String motd1 = null;
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File("config.yml"));
            List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) config.getList("listeners");
            motd1 = list.get(0).get("motd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        motd = motd1;
    }

    @EventHandler
    public void onSLP(ProxyPingEvent event) {
        ServerPing slp = event.getResponse();
        slp.setDescription(Main.renderColors(motd));
        event.setResponse(slp);
    }

}
