package net.redstonecraft.redstoneutils.bungee.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneutils.bungee.obj.LabyModAction;
import net.redstonecraft.redstoneutils.bungee.obj.LabyModSubtitle;
import net.redstonecraft.redstoneutils.bungee.obj.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabyModManager {

    private final SQL sql;
    private final List<ProxiedPlayer> labyModPlayer = new ArrayList<>();

    public LabyModManager(SQL sql) {
        this.sql = sql;
        this.sql.update("CREATE TABLE IF NOT EXISTS labymod (uuid text, subtitle text)");
    }

    public void addPlayer(ProxiedPlayer player) {
        labyModPlayer.add(player);
    }

    public void removePlayer(ProxiedPlayer player) {
        labyModPlayer.remove(player);
    }

    public boolean isLabyModPlayer(ProxiedPlayer player) {
        return labyModPlayer.contains(player);
    }

    public String getSubtitle(User user) {
        PreparedStatement ps = sql.prepareStatement("SELECT * FROM labymod WHERE uuid = ?");
        try {
            ps.setString(1, user.uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed()) {
                if (rs.next()) {
                    return rs.getString("subtitle");
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

    public void setSubtitle(User user, String subtitle) {
        try {
            if (subtitle != null) {
                if (getSubtitle(user) != null) {
                    PreparedStatement ps = sql.prepareStatement("UPDATE labymod SET subtitle = ? WHERE uuid = ?");
                    ps.setString(1, subtitle);
                    ps.setString(2, user.uuid.toString());
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = sql.prepareStatement("INSERT INTO labymod VALUES (?, ?)");
                    ps.setString(1, user.uuid.toString());
                    ps.setString(2, subtitle);
                    ps.executeUpdate();
                }
            } else {
                PreparedStatement ps = sql.prepareStatement("DELETE FROM labymod WHERE uuid = ?");
                ps.setString(1, user.uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setMiddleClickActions(ProxiedPlayer player, Iterable<LabyModAction> actions) {
        JsonArray array = new JsonArray();
        for (LabyModAction i : actions) {
            JsonObject entry = new JsonObject();
            entry.addProperty("displayName", i.displayName);
            entry.addProperty("type", i.type.name());
            entry.addProperty("value", i.value);
            array.add(entry);
        }
        LabyModPlugin.getInstance().sendServerMessage(player, "user_menu_actions", array);
    }

    public static void sendClientToServer(ProxiedPlayer player, String title, String address) {
        JsonObject object = new JsonObject();
        object.addProperty("title", title);
        object.addProperty("address", address);
        object.addProperty("preview", true);
        LabyModPlugin.getInstance().sendServerMessage(player, "server_switch", object);
    }

    public static void sendInputPrompt(ProxiedPlayer player, int promptSessionId, String message, String value, String placeholder, int maxLength) {
        JsonObject object = new JsonObject();
        object.addProperty("id", promptSessionId);
        object.addProperty("message", message);
        object.addProperty("value", value);
        object.addProperty("placeholder", placeholder);
        object.addProperty("max_length", maxLength);
        LabyModPlugin.getInstance().sendServerMessage(player, "input_prompt", object);
    }

    public static void setSubtitles(ProxiedPlayer receiver, Iterable<LabyModSubtitle> subtitles) {
        JsonArray array = new JsonArray();
        for (LabyModSubtitle i : subtitles) {
            JsonObject subtitle = new JsonObject();
            subtitle.addProperty("uuid", i.uuid.toString());
            subtitle.addProperty("size", 1.6d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)
            if (i.subtitle != null) {
                subtitle.addProperty("value", i.subtitle);
            }
            array.add(subtitle);
        }
        LabyModPlugin.getInstance().sendServerMessage(receiver, "account_subtitle", array);
    }

}
