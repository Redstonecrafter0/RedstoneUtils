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

public class TempBanCommand extends Command implements TabExecutor {

    private final BanManager banManager;
    private final UserManager userManager;

    public TempBanCommand(BanManager banManager, UserManager userManager) {
        super("gtempban");
        this.banManager = banManager;
        this.userManager = userManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.tempban")) {
            if (args.length >= 4) {
                try {
                    User user = userManager.getUser(args[0]);
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
                        if (banManager.isBanned(user)) {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &cist bereits gebannt."));
                        } else {
                            boolean success;
                            if (commandSender instanceof ProxiedPlayer) {
                                success = banManager.ban(user, (ProxiedPlayer) commandSender, duration, false, String.join(" ", Arrays.copyOfRange(args, 3, args.length)));
                            } else {
                                success = banManager.consoleBan(user, duration, false, String.join(" ", Arrays.copyOfRange(args, 3, args.length)));
                            }
                            if (success) {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &aerfolgreich gebannt."));
                            } else {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&cBan an &6" + user.name + " &cfehlgeschlagen."));
                            }
                        }
                    }
                } catch (Exception ignored) {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gtempban <user> <duration> <unit: s/m/h/d> <reason>"));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gtempban <user> <duration> <unit: s/m/h/d> <reason>"));
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
