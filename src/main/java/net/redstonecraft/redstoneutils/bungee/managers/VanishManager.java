package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class VanishManager {

    public final List<ProxiedPlayer> vanishedPlayers;

    public VanishManager() {
        vanishedPlayers = new ArrayList<>();
    }

    public void vanish(ProxiedPlayer player) {
        vanishedPlayers.add(player);
    }

    public void unvanish(ProxiedPlayer player) {
        vanishedPlayers.remove(player);
    }

    public boolean isVanished(ProxiedPlayer player) {
        return vanishedPlayers.contains(player);
    }

}
