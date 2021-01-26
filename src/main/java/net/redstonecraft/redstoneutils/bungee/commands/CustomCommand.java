package net.redstonecraft.redstoneutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.redstonecraft.redstoneutils.bungee.Main;

public class CustomCommand extends Command {

    private final String response;

    public CustomCommand(String name, String response) {
        super(name);
        this.response = response;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(Main.renderColors(response));
    }
}
