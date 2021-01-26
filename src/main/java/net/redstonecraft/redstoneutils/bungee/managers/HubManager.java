package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.redstonecraft.redstoneapi.tools.IntUtils;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HubManager {

    private final Main plugin;
    private final List<ServerInfo> lobbiesOnline;

    public HubManager(Main plugin) {
        this.plugin = plugin;
        lobbiesOnline = new ArrayList<>();
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            for (String i : this.plugin.getConfig().getStringList("hubsystem.lobbies")) {
                ServerInfo server = ProxyServer.getInstance().getServerInfo(i);
                new Thread(() -> server.ping((serverPing, throwable) -> {
                    if (serverPing != null) {
                        if (!lobbiesOnline.contains(server)) {
                            lobbiesOnline.add(server);
                        }
                    } else {
                        lobbiesOnline.remove(server);
                    }
                })).start();
            }
        }, 0, plugin.getConfig().getLong("hubsystem.refreshrate"), TimeUnit.SECONDS);
    }

    public ServerInfo getRandomLobby(ServerInfo server) {
        if (server == null) {
            return lobbiesOnline.get(IntUtils.random(0, lobbiesOnline.size() - 1));
        } else {
            if (lobbiesOnline.size() >= 2) {
                ServerInfo lobby;
                do {
                    lobby = lobbiesOnline.get(IntUtils.random(0, lobbiesOnline.size() - 1));
                } while (lobby.equals(server));
                return lobby;
            } else {
                return null;
            }
        }
    }

    public int countLobbiesOnline() {
        return lobbiesOnline.size();
    }

    public boolean contains(ServerInfo serverInfo) {
        return plugin.getConfig().getStringList("hubsystem.lobbies").contains(serverInfo.getName());
    }

}
