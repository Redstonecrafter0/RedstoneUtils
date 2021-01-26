package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.VanishManager;

import java.util.ArrayList;
import java.util.List;

public class PingCommand extends Command implements TabExecutor {

    private final VanishManager vanishManager;

    public PingCommand(VanishManager vanishManager) {
        super("ping");
        this.vanishManager = vanishManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 1 && commandSender.hasPermission("redstoneutilsbungee.command.ping.other") && commandSender.hasPermission("redstoneutilsbungee.command.ping")) {
            perform(commandSender, ProxyServer.getInstance().getPlayer(args[0]));
        } else if (args.length == 0 && commandSender.hasPermission("redstoneutilsbungee.command.ping")) {
            if (commandSender instanceof ProxiedPlayer) {
                perform(commandSender, (ProxiedPlayer) commandSender);
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cDieser Command ist nur für Spieler."));
            }
        }
    }

    private void perform(CommandSender commandSender, ProxiedPlayer player) {
        if (commandSender.equals(player)) {
            commandSender.sendMessage(Main.prefix + Main.renderColors("&aDein Ping ist &6" + player.getPing() + "ms&a."));
        } else {
            if (player != null) {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&aDer Ping von " + player.getName() + " ist &6" + player.getPing() + "ms&a."));
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        boolean run = false;
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            if (p.hasPermission("redstoneutilsbungee.command.ping.other")) {
                run = true;
            }
        } else {
            run = true;
        }
        if (run) {
            List<ProxiedPlayer> list = new ArrayList<>(ProxyServer.getInstance().getPlayers());
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) commandSender;
                if (!player.hasPermission("redstoneutils.vanish.bypass")) {
                    list.removeIf(vanishManager::isVanished);
                }
            }
            List<String> list2 = new ArrayList<>();
            for (ProxiedPlayer i : list) {
                list2.add(i.getName());
            }
            return list2;
        } else {
            return new ArrayList<>();
        }
    }
}
