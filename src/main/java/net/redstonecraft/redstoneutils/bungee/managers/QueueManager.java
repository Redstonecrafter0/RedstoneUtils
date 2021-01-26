package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.enums.QueueMode;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    private final Queue<UUID> queue = new LinkedList<>();
    private final Queue<UUID> priorityQueue = new LinkedList<>();
    private final List<UUID> inQueue = new ArrayList<>();
    private final List<ServerInfo> queueServer = new ArrayList<>();
    private final List<ServerInfo> finalServer = new ArrayList<>();
    public final QueueMode queueMode;
    public final QueueMode finalMode;
    private boolean lastPrioQueue = false;

    public QueueManager(Main plugin) {
        for (String i : plugin.getConfig().getStringList("queue.queueserver")) {
            ServerInfo s = ProxyServer.getInstance().getServerInfo(i);
            if (s != null) {
                queueServer.add(s);
            }
        }
        for (String i : plugin.getConfig().getStringList("queue.finalserver")) {
            ServerInfo s = ProxyServer.getInstance().getServerInfo(i);
            if (s != null) {
                finalServer.add(s);
            }
        }
        queueMode = QueueMode.valueOf(plugin.getConfig().getString("queue.queueprioritymode"));
        finalMode = QueueMode.valueOf(plugin.getConfig().getString("queue.finalprioritymode"));
        if (queueMode == null || finalMode == null) {
            throw new NullPointerException("Unknown Queue Mode");
        }
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            UUID uuid = null;
            if (!lastPrioQueue && priorityQueue.size() > 0) {
                uuid = priorityQueue.remove();
                lastPrioQueue = !lastPrioQueue;
            } else if (lastPrioQueue && queue.size() > 0) {
                uuid = queue.remove();
                lastPrioQueue = !lastPrioQueue;
            } else if (priorityQueue.size() > 0) {
                uuid = priorityQueue.remove();
                lastPrioQueue = true;
            } else if (queue.size() > 0) {
                uuid = queue.remove();
                lastPrioQueue = false;
            }
            if (uuid != null) {
                processQueue(uuid);
            }
        }, 0, plugin.getConfig().getLong("queue.timer"), TimeUnit.MILLISECONDS);
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            for (UUID i : inQueue) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(i);
                if (player != null) {
                    player.sendMessage(Main.prefix + Main.renderColors("&6Du bist in der Queue in Position &a" + inQueue.indexOf(i) + "&6."));
                } else {
                    inQueue.remove(i);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void processQueue(UUID uuid) {
        inQueue.remove(uuid);
        switch (finalMode) {
            case DISTRIBUTE:
                break;
            case LOWEST_FIRST:
                break;
        }
    }

    public void enqueue(ProxiedPlayer player, boolean priority) {
        if (priority) {
            priorityQueue.add(player.getUniqueId());
        } else {
            queue.add(player.getUniqueId());
        }
        inQueue.add(player.getUniqueId());
    }

}
