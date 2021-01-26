package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.managers.BanManager;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;
import net.redstonecraft.redstoneutils.bungee.obj.Ban;

public class BanListener implements Listener {

    private final BanManager banManager;

    public BanListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(LoginEvent event) {
        if (banManager.isBanned(UserManager.fromPendingConnection(event.getConnection()))) {
            Ban ban = banManager.getLatestActiveBan(UserManager.fromPendingConnection(event.getConnection()));
            event.setCancelled(true);
            String[] msg = {
                    " " + Main.prefix,
                    "",
                    ban.isPermanent ? "&cDu bist permanant gebannt." : "&cDu bist noch &6" + Main.renderTimeDiff(ban.until - Main.getCurrentTime()) + " &cgebannt.",
                    "",
                    "&6Von: &f" + (ban.bannedByConsole ? "CONSOLE" : ban.bannedBy.name),
                    "",
                    "&6Grund: &f" + ban.reason
            };
            event.setCancelReason(Main.renderColors(String.join("\n", msg)));
        }
    }

}
