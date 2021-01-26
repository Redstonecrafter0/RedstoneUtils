package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.LabyModManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.enums.LabyModPromts;

public class LabyModSubtitleCommand extends Command {

    private final LabyModManager labyModManager;

    public LabyModSubtitleCommand(LabyModManager labyModManager) {
        super("subtitle");
        this.labyModManager = labyModManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.labymodsubtitle")) {
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) commandSender;
                if (labyModManager.isLabyModPlayer(player)) {
                    String value = labyModManager.getSubtitle(UserManager.fromProxiedPlayer(player));
                    LabyModManager.sendInputPrompt(player, LabyModPromts.SUBTITLE.id, "Lege deinen Subtitle fest.", value == null ? "" : value, "Subtitle", 20);
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cDieser Befehl ist nur für LabyMod Spieler verfügbar."));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cDieser Befehl ist nur für LabyMod Spieler verfügbar."));
            }
        }
    }

}
