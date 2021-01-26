package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayerMods extends Command implements TabExecutor {

    public PlayerMods() {
        super("playermods");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.playermods")) {
            if (args.length == 1) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                if (player != null) {
                    if (player.isForgeUser()) {
                        List<String> mods = new ArrayList<>();
                        for (Map.Entry<String, String> i : player.getModList().entrySet()) {
                            mods.add("&6" + i.getKey() + " " + i.getValue());
                        }
                        if (mods.size() > 0) {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&aDie Mods von " + player.getName() + " sind:\n" + String.join("\n", mods)));
                        } else {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&cKeine Mods für den Spieler " + player.getName() + " gefunden aber der Spieler benutzt Forge."));
                        }
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&cDer Spieler " + player.getName() + " benutzt kein Forge."));
                    }
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cGebe einen Spieler an."));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.playermods")) {
            List<String> playerNames = new ArrayList<>();
            for (ProxiedPlayer i : ProxyServer.getInstance().getPlayers()) {
                playerNames.add(i.getName());
            }
            Collections.sort(playerNames);
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }
}
