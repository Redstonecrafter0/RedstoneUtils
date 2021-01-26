package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportManager {

    private final HashMap<UUID, UUID> reports = new HashMap<>();
    private final List<ProxiedPlayer> loggedIn = new ArrayList<>();
    private final Main plugin;

    public ReportManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean report(ProxiedPlayer reported, ProxiedPlayer reportedBy) {
        if (reports.containsValue(reportedBy.getUniqueId())) {
            return false;
        } else {
            reports.put(reported.getUniqueId(), reportedBy.getUniqueId());
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> reports.remove(reported), 0, plugin.getConfig().getLong("report.timeout"), TimeUnit.SECONDS);
            return true;
        }
    }

    public void remove(ProxiedPlayer reported, boolean accepted) {
        ProxiedPlayer reportedBy = ProxyServer.getInstance().getPlayer(reports.get(reported.getUniqueId()));
        if (reportedBy != null) {
            if (accepted) {
                reportedBy.sendMessage(Main.prefix + Main.renderColors("&aDein Report an &6" + reported.getName() + " &awird nun bearbeitet."));
            }
        }
        reports.remove(reported.getUniqueId());
    }

}
