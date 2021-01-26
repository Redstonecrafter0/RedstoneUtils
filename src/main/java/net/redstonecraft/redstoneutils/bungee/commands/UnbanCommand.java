package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.BanManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.User;

public class UnbanCommand extends Command {

    private final BanManager banManager;
    private final UserManager userManager;

    public UnbanCommand(BanManager banManager, UserManager userManager) {
        super("gunban");
        this.banManager = banManager;
        this.userManager = userManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.unban")) {
            if (args.length == 1) {
                User user = userManager.getUser(args[0]);
                if (user != null) {
                    if (banManager.isBanned(user)) {
                        if (commandSender instanceof ProxiedPlayer) {
                            if (banManager.unban(user, (ProxiedPlayer) commandSender)) {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &awurde entbannt."));
                            } else {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &ckonnte nicht entbannt werden."));
                            }
                        } else {
                            if (banManager.consoleUnban(user)) {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &awurde entbannt."));
                            } else {
                                commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &ckonnte nicht entbannt werden."));
                            }
                        }
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &cwar nicht gebannt."));
                    }
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gunban <user>"));
            }
        }
    }
}
