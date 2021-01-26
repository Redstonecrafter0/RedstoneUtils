package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.MuteManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MuteCommand extends Command implements TabExecutor {

    private final MuteManager muteManager;

    public MuteCommand(MuteManager muteManager) {
        super("gmute");
        this.muteManager = muteManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.mute")) {
            if (args.length >= 4) {
                try {
                    int duration = Integer.parseInt(args[1]);
                    boolean a;
                    switch (args[2]) {
                        case "s":
                            a = true;
                            break;
                        case "m":
                            a = true;
                            duration = duration * 60;
                            break;
                        case "h":
                            a = true;
                            duration = duration * 60 * 60;
                            break;
                        case "d":
                            a = true;
                            duration = duration * 60 * 60 * 24;
                            break;
                        default:
                            a = false;
                            break;
                    }
                    if (a) {
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                        if (player != null) {
                            boolean worked = muteManager.mute(UserManager.fromProxiedPlayer(player), commandSender instanceof ProxiedPlayer ? (ProxiedPlayer) commandSender : null, duration, String.join(" ", Arrays.copyOfRange(args, 3, args.length)));
                            if (worked) {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + player.getName() + " &awurde gemuted."));
                            } else {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + player.getName() + " &ckonnte nicht gemuted werden."));
                            }
                        } else {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
                        }
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gmute <user> <duration> <unit: s/m/h/d> <reason>"));
                    }
                } catch (Exception ignored) {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gmute <user> <duration> <unit: s/m/h/d> <reason>"));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gmute <user> <duration> <unit: s/m/h/d> <reason>"));
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
