package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.tools.HttpRequest;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AntiBot implements Listener {

    private List<String> whitelisted = new ArrayList<>();
    private List<String> whitelisted2 = new ArrayList<>();
    private List<String> whitelisted3 = new ArrayList<>();
    private List<String> blacklist = new ArrayList<>();
    private List<String> blacklist2 = new ArrayList<>();
    private final HashMap<String, Integer> players = new HashMap<>();
    private final boolean vpnCheck;
    private final boolean cooldownEnabled;
    private final boolean motdFirst;
    private final boolean joinTwice;
    private final boolean perPlayerCooldown;
    private final boolean maxPlayersPerIp;
    private final int maxPlayersPerIpCount;
    private boolean slowdown = false;

    public AntiBot(Main plugin) {
        cooldownEnabled = plugin.getConfig().getBoolean("antibot.totalcooldown.enabled");
        perPlayerCooldown = plugin.getConfig().getBoolean("antibot.perplayercooldown.enabled");
        vpnCheck = plugin.getConfig().getBoolean("antibot.vpncheck.enabled");
        motdFirst = plugin.getConfig().getBoolean("antibot.motdfirst.enabled");
        joinTwice = plugin.getConfig().getBoolean("antibot.jointwice.enabled");
        maxPlayersPerIp = plugin.getConfig().getBoolean("antibot.maxplayersperip.enabled");
        maxPlayersPerIpCount = plugin.getConfig().getInt("antibot.maxplayersperip.count");
        if (vpnCheck) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
                blacklist2 = new ArrayList<>();
                whitelisted3 = new ArrayList<>();
            }, 0, plugin.getConfig().getLong("antibot.vpncheck.persistence"), TimeUnit.SECONDS);
        }
        if (cooldownEnabled) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> slowdown = false, 0,plugin.getConfig().getLong("antibot.totalcooldown.value"), TimeUnit.SECONDS);
        }
        if (motdFirst) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
                whitelisted = new ArrayList<>();
                whitelisted2 = new ArrayList<>();
            }, 0, plugin.getConfig().getLong("antibot.motdfirst.persistence"), TimeUnit.SECONDS);
        }
        if (joinTwice) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
                whitelisted = new ArrayList<>();
                whitelisted2 = new ArrayList<>();
            }, 0, plugin.getConfig().getLong("antibot.jointwice.persistence"), TimeUnit.SECONDS);
        }
        if (perPlayerCooldown) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> blacklist = new ArrayList<>(), 0,10, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void onSLP(ProxyPingEvent event) {
        if (motdFirst) {
            if (!whitelisted.contains(event.getConnection().getAddress().getHostName())) {
                whitelisted.add(event.getConnection().getAddress().getHostName());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        if (maxPlayersPerIp) {
            if (players.containsKey(event.getConnection().getAddress().getHostName())) {
                players.put(event.getConnection().getAddress().getHostName(), players.get(event.getConnection().getAddress().getHostName()) + 1);
            } else {
                players.put(event.getConnection().getAddress().getHostName(), 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogout(PlayerDisconnectEvent event) {
        if (maxPlayersPerIp) {
            int c = players.get(event.getPlayer().getAddress().getHostName());
            if (c == 1) {
                players.remove(event.getPlayer().getAddress().getHostName());
            } else {
                players.put(event.getPlayer().getAddress().getHostName(), c - 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event) {
        if (motdFirst && !whitelisted.contains(event.getConnection().getAddress().getHostName())) {
            event.setCancelled(true);
            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6F" + new String(new byte[]{(byte) 0xFC}, StandardCharsets.UTF_8) + "ge den Server erst zur Serverliste hinzu um die MOTD zu sehen."));
        } else if (joinTwice && !whitelisted2.contains(event.getConnection().getAddress().getHostName())) {
            event.setCancelled(true);
            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6IP-Adresse verifiziert.\n&6Verbinde dich erneut um der Server beizutreten."));
            whitelisted2.add(event.getConnection().getAddress().getHostName());
        } else if (perPlayerCooldown && blacklist.contains(event.getConnection().getAddress().getHostName())) {
            event.setCancelled(true);
            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6Cooldown:\n&6Versuche es wieder in &c10 &6Sekunden."));
        } else if (slowdown) {
            event.setCancelled(true);
            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6Cooldown:\n&6Versuche es in ein paar Sekunden wieder."));
        } else if (vpnCheck && blacklist2.contains(event.getConnection().getAddress().getHostName())) {
            event.setCancelled(true);
            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6VPN erkannt. Bitte deaktiviere deine VPN bevor du versuchst dich neu zu verbinden."));
        } else if (vpnCheck && !blacklist2.contains(event.getConnection().getAddress().getHostName()) && !whitelisted3.contains(event.getConnection().getAddress().getHostName())) {
            try {
                HttpRequest req = HttpRequest.get("https://ip.teoh.io/api/vpn/" + URLEncoder.encode(event.getConnection().getAddress().getHostName(), StandardCharsets.UTF_8.toString()));
                try {
                    if (req.responseCode == 200) {
                        JSONObject resp = JSONParser.parseObject(new String(req.content, StandardCharsets.UTF_8));
                        if (resp.getString("vpn_or_proxy").equals("yes")) {
                            event.setCancelled(true);
                            event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6VPN erkannt. Bitte deaktiviere deine VPN bevor du versuchst dich neu zu verbinden."));
                            blacklist2.add(event.getConnection().getAddress().getHostName());
                        } else {
                            whitelisted3.add(event.getConnection().getAddress().getHostName());
                        }
                    }
                } catch (Exception ignored) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (maxPlayersPerIp && players.containsKey(event.getConnection().getAddress().getHostName())) {
            if (players.get(event.getConnection().getAddress().getHostName()) > maxPlayersPerIpCount) {
                event.setCancelled(true);
                event.setCancelReason(" " + Main.prefix + Main.renderColors("\n\n&6Zu viele Logins mit der gleichen IP-Addresse erkannt."));
            }
        } else if (perPlayerCooldown) {
            blacklist.add(event.getConnection().getAddress().getHostName());
        }
        if (cooldownEnabled && !slowdown) {
            slowdown = true;
        }
    }

}
