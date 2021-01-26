package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.redstonecraft.redstoneutils.bungee.Main;

import java.util.ArrayList;
import java.util.List;

public class RedstoneUtilsCommand extends Command implements TabExecutor {

    private final Main plugin;

    public RedstoneUtilsCommand(Main plugin) {
        super("gredstoneutils");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("redstoneutilsbungee.command.redstoneutils")) {
            if (args.length == 1) {
                if (commandSender.hasPermission("redstoneutilsbungee.command.redstoneutils.reload")) {
                    new Thread(() -> {
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&aPlugin wird neu geladen."));
                        plugin.onDisable();
                        plugin.onEnable();
                        commandSender.sendMessage(Main.prefix + Main.renderColors("&aPlugin neu geladen."));
                    }).start();
                } else {
                    commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gredstoneutils reload"));
                }
            } else {
                commandSender.sendMessage(Main.prefix + Main.renderColors("&cUsage: /gredstoneutils reload"));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> list = new ArrayList<>();
        list.add("reload");
        return list;
    }
}
