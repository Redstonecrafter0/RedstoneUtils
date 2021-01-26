package net.redstonecraft.redstoneutils.bungee.managers;

import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneutils.bungee.Main;
import net.redstonecraft.redstoneutils.bungee.obj.Ban;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BanManager {

    private final SQL sql;
    private final UserManager userManager;
    private final boolean luckPermsProvided;

    public BanManager(SQL sql, UserManager userManager) {
        this.sql = sql;
        this.userManager = userManager;
        this.sql.update("CREATE TABLE IF NOT EXISTS bans (uuid text, since text, until text, bannedby text, reason text, unbanned text, unbannedBy text)");
        luckPermsProvided = ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") != null;
    }

    public boolean isBanned(User user) {
        refreshBans(user);
        return getActiveBans(user).size() >= 1;
    }

    public void refreshBans(User user) {
        List<Ban> list = getActiveBans(user);
        for (Ban i : list) {
            if (Main.getCurrentTime() > i.until && !i.isPermanent) {
                PreparedStatement ps = sql.prepareStatement("UPDATE bans SET unbanned = ?, unbannedBy = ? WHERE uuid = ? AND since = ? AND until = ? AND reason = ?");
                try {
                    ps.setString(1, "true");
                    ps.setString(2, "AUTO");
                    ps.setString(3, i.bannedPlayer.uuid.toString());
                    ps.setString(4, String.valueOf(i.since));
                    ps.setString(5, String.valueOf(i.until));
                    ps.setString(6, i.reason);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Ban> getBans(User user) {
        PreparedStatement ps = sql.prepareStatement("SELECT * FROM bans WHERE uuid = ?");
        try {
            ps.setString(1, user.uuid.toString());
            ResultSet rs = ps.executeQuery();
            List<Ban> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Ban(user, rs.getString("bannedby").equals("CONSOLE") || rs.getString("bannedby").equals("AUTO")? null : userManager.getUser(UUID.fromString(rs.getString("bannedby"))), Long.parseLong(rs.getString("since")), Long.parseLong(rs.getString("until")), Boolean.parseBoolean(rs.getString("unbanned")), rs.getString("reason"), rs.getString("unbannedBy").equals("CONSOLE") || rs.getString("bannedby").equals("AUTO") ? null : userManager.getUser(rs.getString("unbannedBy")), rs.getString("unbannedby")));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Ban> getActiveBans(User user) {
        List<Ban> list = new ArrayList<>();
        for (Ban i : getBans(user)) {
            if (!i.unbanned) {
                list.add(i);
            }
        }
        return list;
    }

    public Ban getLatestBan(User user) {
        refreshBans(user);
        List<Ban> list = getBans(user);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return null;
        } else {
            Ban ban = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                Ban b = list.get(i);
                if (b.since > ban.since) {
                    ban = b;
                }
            }
            return ban;
        }
    }

    public Ban getLatestActiveBan(User user) {
        refreshBans(user);
        List<Ban> list = getActiveBans(user);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return null;
        } else {
            Ban ban = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                Ban b = list.get(i);
                if (b.until > ban.until) {
                    ban = b;
                }
            }
            return ban;
        }
    }

    private static boolean luckPermsCheck(User toBan, ProxiedPlayer bannedBy) {
        try {
            int toBanWeight = Objects.requireNonNull(LuckPermsProvider.get().getGroupManager().getGroup(Objects.requireNonNull(LuckPermsProvider.get().getUserManager().getUser(toBan.uuid)).getPrimaryGroup())).getWeight().getAsInt();
            int bannedByWeight = Objects.requireNonNull(LuckPermsProvider.get().getGroupManager().getGroup(Objects.requireNonNull(LuckPermsProvider.get().getUserManager().getUser(bannedBy.getUniqueId())).getPrimaryGroup())).getWeight().getAsInt();
            return toBanWeight < bannedByWeight;
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean ban(User toBan, ProxiedPlayer bannedBy, long duration, boolean permanent, String reason) {
        if (!isBanned(toBan)) {
            if (luckPermsProvided) {
                if (luckPermsCheck(toBan, bannedBy)) {
                    internalBan(toBan, bannedBy, duration, permanent, reason);
                    return true;
                } else {
                    return false;
                }
            } else {
                internalBan(toBan, bannedBy, duration, permanent, reason);
                return true;
            }
        } else {
            return false;
        }
    }

    private void internalBan(User toBan, ProxiedPlayer bannedBy, long duration, boolean permanent, String reason) {
        PreparedStatement ps = sql.prepareStatement("INSERT INTO bans VALUES (?, ?, ?, ?, ?, ?, ?)");
        try {
            long timestamp = Main.getCurrentTime();
            ps.setString(1, toBan.uuid.toString());
            ps.setString(2, String.valueOf(timestamp));
            ps.setString(3, permanent ? "0" : String.valueOf(timestamp + duration));
            ps.setString(4, bannedBy.getUniqueId().toString());
            ps.setString(5, reason);
            ps.setString(6, "false");
            ps.setString(7, "null");
            ps.executeUpdate();
            kickIfOnline(toBan);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean consoleBan(User toBan, long duration, boolean permanent, String reason) {
        if (!isBanned(toBan)) {
            internalConsoleBan(toBan, duration, permanent, reason);
            return true;
        } else {
            return false;
        }
    }

    private void internalConsoleBan(User toBan, long duration, boolean permanent, String reason) {
        PreparedStatement ps = sql.prepareStatement("INSERT INTO bans VALUES (?, ?, ?, ?, ?, ?, ?)");
        try {
            long timestamp = Main.getCurrentTime();
            ps.setString(1, toBan.uuid.toString());
            ps.setString(2, String.valueOf(timestamp));
            ps.setString(3, permanent ? "0" : String.valueOf(timestamp + duration));
            ps.setString(4, "CONSOLE");
            ps.setString(5, reason);
            ps.setString(6, "false");
            ps.setString(7, "null");
            ps.executeUpdate();
            kickIfOnline(toBan);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void kickIfOnline(User user) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(user.name);
        if (player != null) {
            player.disconnect(" " + Main.prefix + Main.renderColors("\n\n&cDu wurdest gebannt."));
        }
    }

    public boolean unban(User bannedUser, ProxiedPlayer unbannedBy) {
        if (isBanned(bannedUser)) {
            PreparedStatement ps = sql.prepareStatement("UPDATE bans SET unbanned = ?, unbannedBy = ? WHERE uuid = ? AND unbanned = ?");
            try {
                ps.setString(1, "true");
                ps.setString(2, unbannedBy.getUniqueId().toString());
                ps.setString(3, bannedUser.uuid.toString());
                ps.setString(4, "false");
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean consoleUnban(User bannedUser) {
        if (isBanned(bannedUser)) {
            PreparedStatement ps = sql.prepareStatement("UPDATE bans SET unbanned = ?, unbannedBy = ? WHERE uuid = ? AND unbanned = ?");
            try {
                ps.setString(1, "true");
                ps.setString(2, "CONSOLE");
                ps.setString(3, bannedUser.uuid.toString());
                ps.setString(4, "false");
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

}
