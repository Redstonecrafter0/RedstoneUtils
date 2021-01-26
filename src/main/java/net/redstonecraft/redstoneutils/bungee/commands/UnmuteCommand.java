package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.MuteManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.User;

public class UnmuteCommand extends Command {

    private final MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
        super("gunmute");
        this.muteManager = muteManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.unmute")) {
            if (args.length == 1) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                if (player != null) {
                    User user = UserManager.fromProxiedPlayer(player);
                    if (muteManager.isMuted(user)) {
                        if (muteManager.unmute(user)) {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &awurde entmuted."));
                        } else {
                            commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &ckonnte nicht entmutet werden."));
                        }
                    } else {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &cwar nicht gemuted."));
                    }
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gunmute <user>"));
            }
        }
    }
}
