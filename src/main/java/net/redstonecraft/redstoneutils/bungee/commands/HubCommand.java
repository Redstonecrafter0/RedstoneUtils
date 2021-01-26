package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.HubManager;

import java.util.Arrays;
import java.util.List;

public class HubCommand extends Command {

    private final HubManager hubManager;

    public HubCommand(HubManager hubManager, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.hubManager = hubManager;
    }

    public static HubCommand createCommand(Main plugin, HubManager hubManager) {
        List<String> list = plugin.getConfig().getStringList("hubsystem.aliases");
        String name;
        String[] aliases;
        if (list.size() > 1) {
            name = list.get(0);
            aliases = Arrays.copyOfRange(list.toArray(new String[]{}), 1, list.size());
        } else if (list.size() == 1) {
            name = list.get(0);
            aliases = new String[]{};
        } else {
            name = "lobby";
            aliases = new String[]{};
        }
        return new HubCommand(hubManager, name, "redstoneutilsbungee.command.hub", aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (!hubManager.contains(player.getServer().getInfo())) {
                if (hubManager.countLobbiesOnline() > 0) {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cConnecting to Lobby..."));
                    player.connect(hubManager.getRandomLobby(null));
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cEs ist zur Zeit keine Lobby online."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cDu bist bereits auf einer Lobby."));
            }
        } else {
            commandSender.sendMessage(Main.prefix + Main.renderColors("&cDieser Befehl ist nur für Spieler."));
        }
    }
}
