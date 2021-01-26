package net.redstonecraft.redstoneutils.bungee.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.redstonecraft.redstoneutils.bungee.managers.UserManager;

public class UserListener implements Listener {

    private final UserManager userManager;

    public UserListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent event) {
        userManager.updateUser(UserManager.fromPendingConnection(event.getConnection()));
    }

}
