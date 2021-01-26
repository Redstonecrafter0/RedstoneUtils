package net.redstonecraft.redstoneutils.bungee.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.tools.MojangAPI;
import net.redstonecraft.redstoneapi.tools.mojangapi.MojangProfile;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserManager {

    private final SQL sql;

    public UserManager(SQL sql) {
        this.sql = sql;
        this.sql.update("CREATE TABLE IF NOT EXISTS players (uuid text, name text)");
    }

    public boolean isRegistered(User user) {
        return getUser(user.uuid) != null;
    }

    public User getUser(UUID uuid) {
        PreparedStatement ps = sql.prepareStatement("SELECT * FROM players WHERE uuid = ?");
        try {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed()) {
                if (rs.next()) {
                    return new User(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String name) {
        User user = getUserInternal(name);
        if (user == null) {
            user = fetch(name);
            if (user != null) {
                registerUser(user);
            }
        }
        return user;
    }

    private User getUserInternal(String name) {
        PreparedStatement ps = sql.prepareStatement("SELECT * FROM players WHERE LOWER(name) = ?");
        try {
            ps.setString(1, name.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed()) {
                int c = 0;
                while (rs.next()) {
                    c++;
                }
                if (c == 1) {
                    rs.first();
                    return new User(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerUser(User user) {
        if (!isRegistered(user)) {
            PreparedStatement ps = sql.prepareStatement("INSERT INTO players VALUES (?, ?)");
            try {
                ps.setString(1, user.uuid.toString());
                ps.setString(2, user.name);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUser(User user) {
        registerUser(user);
        PreparedStatement ps = sql.prepareStatement("UPDATE players SET name = ? WHERE uuid = ?");
        try {
            ps.setString(1, user.name);
            ps.setString(2, user.uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isOnline(User user) {
        return ProxyServer.getInstance().getPlayer(user.uuid) != null;
    }

    public static User fromProxiedPlayer(ProxiedPlayer player) {
        return new User(player.getUniqueId(), player.getName());
    }

    public static User fromPendingConnection(PendingConnection connection) {
        return new User(connection.getUniqueId(), connection.getName());
    }

    private static User fetch(String name) {
        UUID uuid = MojangAPI.getUnigueIdByName(name);
        if (uuid != null) {
            return new User(uuid, name);
        }
        return null;
    }

    private static User fetch(UUID uuid) {
        MojangProfile profile = MojangAPI.getProfile(uuid);
        if (profile != null) {
            return new User(uuid, profile.getName());
        }
        return null;
    }

}
