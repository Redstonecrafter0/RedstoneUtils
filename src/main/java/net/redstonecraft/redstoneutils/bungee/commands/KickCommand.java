package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KickCommand extends Command implements TabExecutor {

    public KickCommand() {
        super("gkick");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.kick")) {
            if (args.length >= 2) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                if (player != null) {
                    if (!player.hasPermission("redstoneutilsbungee.command.kick.bypass")) {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + player.getName() + " &awurde gekickt."));
                        player.disconnect(" " + Main.prefix + Main.renderColors("\n\n" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replace("\\n", "\n")));
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&cDu kannst &6" + player.getName() + " &anicht kicken."));
                    }
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cDieser Spieler ist nicht online."));
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> list = new ArrayList<>();
        for (ProxiedPlayer i : ProxyServer.getInstance().getPlayers()) {
            list.add(i.getName());
        }
        return list;
    }
}
