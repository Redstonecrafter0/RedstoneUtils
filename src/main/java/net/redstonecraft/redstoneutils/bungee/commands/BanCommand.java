package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.BanManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BanCommand extends Command implements TabExecutor {

    private final BanManager banManager;
    private final UserManager userManager;

    public BanCommand(BanManager banManager, UserManager userManager) {
        super("gban");
        this.banManager = banManager;
        this.userManager = userManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.ban")) {
            if (args.length >= 2) {
                User user = userManager.getUser(args[0]);
                if (banManager.isBanned(user)) {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &cist bereits gebannt."));
                } else {
                    boolean success;
                    if (commandSender instanceof ProxiedPlayer) {
                        success = banManager.ban(user, (ProxiedPlayer) commandSender, 0, true, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    } else {
                        success = banManager.consoleBan(user, 0, true, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    }
                    if (success) {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &aerfolgreich gebannt."));
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&cBan an &6" + user.name + " &cfehlgeschlagen."));
                    }
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gban <user> <reason>"));
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
