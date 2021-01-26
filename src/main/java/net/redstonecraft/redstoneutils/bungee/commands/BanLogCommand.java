package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.BanManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.Ban;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.util.ArrayList;
import java.util.List;

public class BanLogCommand extends Command {

    private final BanManager banManager;
    private final UserManager userManager;

    public BanLogCommand(BanManager banManager, UserManager userManager) {
        super("gbanlog");
        this.banManager = banManager;
        this.userManager = userManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.ban.log")) {
            if (args.length == 1) {
                User user = userManager.getUser(args[0]);
                if (user != null) {
                    List<Ban> list = banManager.getBans(user);
                    if (list.size() == 0) {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&6" + user.name + " &awurde nie gebannt."));
                    } else {
                        List<String> lines = new ArrayList<>();
                        for (Ban i : list) {
                            String s = i.bannedByConsole ? "CONSOLE" : i.bannedBy.name;
                            if (!i.isPermanent) {
                                lines.add("&a - Gebannt vom &6" + Main.renderTime(i.since) + " &a bis &6" + Main.renderTime(i.until) + " &a von &6" + s + (i.unbanned ? " &a(Entbannt von &6" + (i.unbannedByConsole || i.unbannedAuto ? (i.unbannedByConsole ? "CONSOLE" : "AUTO") : i.unbannedBy.name) + "&a)" : ""));
                            } else {
                                lines.add("&a - Permanent gebannt von &6" + s + (i.unbanned ? " &a(Entbannt von &6" + (i.unbannedByConsole ? "CONSOLE" : i.unbannedBy.name) + "&a)" : ""));
                            }
                        }
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&aBanlogs von &6" + user.name + "&a:\n" + String.join("\n", lines)));
                    }
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cSpieler nicht gefunden."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gbanlog <user>"));
            }
        }
    }

}
