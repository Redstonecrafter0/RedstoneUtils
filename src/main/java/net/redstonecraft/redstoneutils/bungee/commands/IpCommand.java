package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.List;

public class IpCommand extends Command implements TabExecutor {
    public IpCommand() {
        super("getip");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.ip")) {
            if (args.length == 1) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                if (player != null) {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&aDie IP von &6" + player.getName() + " &a ist &9" + player.getAddress().getHostName() + "&a."));
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cGebe einen Spieler an."));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.ip")) {
            List<String> list = new ArrayList<>();
            for (ProxiedPlayer i : ProxyServer.getInstance().getPlayers()) {
                list.add(i.getName());
            }
            return list;
        }
        return new ArrayList<>();
    }
}
