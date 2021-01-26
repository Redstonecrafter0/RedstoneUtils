package net.redstonecraft.redstoneutils.bungee.managers;

import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.events.MuteEvent;
import net.redstonecraft.redstoneutils.bungee.obj.Mute;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MuteManager {
    
    private final SQL sql;
    private final UserManager userManager;
    private final boolean luckPermsProvided;

    public MuteManager(SQL sql, UserManager userManager) {
        this.sql = sql;
        this.userManager = userManager;
        this.sql.update("CREATE TABLE IF NOT EXISTS mutes (uuid text, since text, until text, mutedby text, reason text)");
        luckPermsProvided = ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") != null;
    }

    public boolean isMuted(User user) {
        return getMutes(user).size() != 0;
    }
    
    public Mute getLatestMute(User user) {
        List<Mute> list = getMutes(user);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return null;
        } else {
            Mute mute = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                if (mute.until < list.get(i).until) {
                    mute = list.get(i);
                }
            }
            return mute;
        }
    }

    public List<Mute> getMutes(User user) {
        PreparedStatement ps = sql.prepareStatement("SELECT * FROM mutes WHERE uuid = ?");
        try {
            ps.setString(1, user.uuid.toString());
            ResultSet rs = ps.executeQuery();
            List<Mute> list = new ArrayList<>();
            boolean a = false;
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String by = rs.getString("mutedby");
                long since = Long.parseLong(rs.getString("since"));
                long until = Long.parseLong(rs.getString("until"));
                if (until < Main.getCurrentTime()) {
                    a = true;
                } else {
                    list.add(new Mute(userManager.getUser(UUID.fromString(uuid)), by.equals("CONSOLE") ? null : userManager.getUser(UUID.fromString(by)), since, until, by.equals("CONSOLE"), rs.getString("reason")));
                }
            }
            if (a) {
                PreparedStatement ps1 = sql.prepareStatement("DELETE FROM mutes WHERE until < ?");
                ps1.setString(1, String.valueOf(Main.getCurrentTime()));
                ps1.executeUpdate();
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean mute(User user, ProxiedPlayer by, long duration, String reason) {
        ProxiedPlayer a = ProxyServer.getInstance().getPlayer(user.name);
        if (a != null) {
            if (a.hasPermission("redstoneutilsbungee.command.mute.bypass")) {
                return false;
            }
        }
        if (luckPermsProvided && by != null) {
            if (!luckPermsCheck(user, by)) {
                return false;
            }
        }
        PreparedStatement ps = sql.prepareStatement("INSERT INTO mutes VALUES (?, ?, ?, ?, ?)");
        try {
            long timestamp = Main.getCurrentTime();
            ps.setString(1, user.uuid.toString());
            ps.setString(2, String.valueOf(timestamp));
            ps.setString(3, String.valueOf(timestamp + duration));
            ps.setString(4, by != null ? by.getUniqueId().toString() : "CONSOLE");
            ps.setString(5, reason);
            ps.executeUpdate();
            ProxyServer.getInstance().getPluginManager().callEvent(new MuteEvent(new Mute(user, by != null ? UserManager.fromProxiedPlayer(by) : null, 0, 0, by != null, reason)));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unmute(User user) {
        PreparedStatement ps = sql.prepareStatement("DELETE FROM DELETE FROM mutes WHERE uuid = ?");
        try {
            ps.setString(1, user.uuid.toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean luckPermsCheck(User toMute, ProxiedPlayer mutedBy) {
        try {
            int toMuteWeight = Objects.requireNonNull(LuckPermsProvider.get().getGroupManager().getGroup(Objects.requireNonNull(LuckPermsProvider.get().getUserManager().getUser(toMute.uuid)).getPrimaryGroup())).getWeight().getAsInt();
            int mutedByWeight = Objects.requireNonNull(LuckPermsProvider.get().getGroupManager().getGroup(Objects.requireNonNull(LuckPermsProvider.get().getUserManager().getUser(mutedBy.getUniqueId())).getPrimaryGroup())).getWeight().getAsInt();
            return toMuteWeight < mutedByWeight;
        } catch (Exception ignored) {
        }
        return false;
    }
    
}
