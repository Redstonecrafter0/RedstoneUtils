package net.redstonecraft.redstoneutils.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.redstonecraft.redstoneapi.ipc.IPCServer;
import net.redstonecraft.redstoneapi.sql.*;
import net.redstonecraft.redstoneapi.tools.StringUtils;
import net.redstonecraft.redstoneutils.bungee.commands.*;
import net.redstonecraft.redstoneutils.bungee.listeners.*;
import net.redstonecraft.redstoneutils.bungee.managers.*;
import net.redstonecraft.redstoneutils.bungee.processors.*;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Plugin {

    public static final String prefix = renderColors("&f[&9Redstone&cUtils&f] &r");

    private Configuration config;
    private IPCServer ipcServer;
    private SQL sql;

    @Override
    public void onLoad() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cUtils&f-&6Bungee &fv" + getDescription().getVersion() + " &aloaded. &b║",
                "&b║" + StringUtils.sameChar(' ', 32 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 12 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        try {
            ipcServer = new IPCServer(config.getString("ipc.host"), config.getInt("ipc.port"), config.getString("ipc.token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (SQLTypes.valueOf(config.getString("database"))) {
            case MYSQL:
                try {
                    sql = new MySQL(config.getString("mysql.host"), config.getInt("mysql.port"), config.getString("mysql.database"), config.getString("mysql.username"), config.getString("mysql.password"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case SQLITE:
                try {
                    sql = new SQLite(config.getString("sqlitedbName"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new NullPointerException("Undefined Database Format");
        }
        HubManager hubManager = new HubManager(this);
        registerListener(new HubListener(hubManager));
        registerCommand(HubCommand.createCommand(this, hubManager));

        VanishManager vanishManager = new VanishManager();
        ipcServer.addProcessor(new PlayerVanishProcessor(vanishManager));

        UserManager userManager = new UserManager(sql);
        registerListener(new UserListener(userManager));

        BanManager banManager = new BanManager(sql, userManager);
        registerListener(new BanListener(banManager));
        registerCommand(new BanCommand(banManager, userManager));
        registerCommand(new TempBanCommand(banManager, userManager));
        registerCommand(new BanLogCommand(banManager, userManager));
        registerCommand(new UnbanCommand(banManager, userManager));

        MuteManager muteManager = new MuteManager(sql, userManager);
        registerListener(new MuteListener(muteManager));
        registerCommand(new MuteCommand(muteManager));
        registerCommand(new UnmuteCommand(muteManager));

        if (config.getBoolean("chatcooldown.enabled")) {
            ChatManager chatManager = new ChatManager(this);
            registerListener(new AntiSpam(chatManager));
            registerPermissions("redstoneutils.chat.bypass");
        }

        registerListener(new AntiDDOS(this));
        registerListener(new AntiBot(this));
        registerListener(new CorrectMotdListener());

        if (config.getBoolean("customversion.enabled")) {
            registerListener(new CustomVersionListener(config.getString("customversion.value"), config.getBoolean("customversion.showalways")));
        }

        registerCommand(new PlayerMods());
        registerCommand(new PingCommand(vanishManager));
        registerCommand(new IpCommand());
        registerCommand(new KickCommand());
        registerCommand(new RedstoneUtilsCommand(this));

        if (ProxyServer.getInstance().getPluginManager().getPlugin("LabyModAPI") != null) {
            LabyModManager labyModManager = new LabyModManager(sql);
            registerListener(new LabyModListener(labyModManager));
            registerListener(new LabyModVoiceChatMuteListener(muteManager, labyModManager, this));
            registerListener(new LabyModDiscordListener(this, config.getString("domain"), labyModManager));
            registerListener(new LabyModSubtitles(labyModManager));
            registerListener(new LabyModGamemodes(labyModManager));
            registerCommand(new LabyModSubtitleCommand(labyModManager));
            registerListener(new LabyModSubtitleListener(labyModManager, this));
            registerPermissions("redstoneutilsbungee.command.labymodsubtitle");
        }

        for (String i : ((Configuration) config.get("customcommands")).getKeys()) {
            registerCommand(new CustomCommand(i, String.join("\n", config.getStringList("customcommands." + i))));
        }

        registerPermissions(
                "redstoneutilsbungee.command.playermods",
                "redstoneutilsbungee.command.hub",
                "redstoneutilsbungee.command.ping",
                "redstoneutilsbungee.command.ping.other",
                "redstoneutilsbungee.command.ip",
                "redstoneutilsbungee.command.ban",
                "redstoneutilsbungee.command.unban",
                "redstoneutilsbungee.command.ban.log",
                "redstoneutilsbungee.command.tempban",
                "redstoneutilsbungee.command.kick",
                "redstoneutilsbungee.command.kick.bypass",
                "redstoneutilsbungee.command.mute",
                "redstoneutilsbungee.command.unmute",
                "redstoneutilsbungee.command.mute.bypass",
                "redstoneutilsbungee.command.redstoneutils",
                "redstoneutilsbungee.command.redstoneutils.reload",
                "redstoneutils.vanish.bypass",
                "redstoneutils.chat.color"
        );

        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 33 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cUtils&f-&6Bungee &fv" + getDescription().getVersion() + " &aenabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 33 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 13 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 33 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getScheduler().cancel(this);
        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
        ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
        try {
            ipcServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sql.close();
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 34 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cUtils&f-&6Bungee &fv" + getDescription().getVersion() + " &cdisabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 34 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 14 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 34 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    private static void registerPermissions(String... permissions) {
        for (String i : permissions) {
            ProxyServer.getInstance().getConsole().hasPermission(i);
        }
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public static String renderColors(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String renderTimeDiff(long timestamp) {
        if (timestamp < 0) {
            timestamp = 0;
        }
        long seconds = timestamp % 60;
        long minutes = timestamp / 60 % 60;
        long hours = timestamp / 60 / 60 % 24;
        long days = timestamp / 60 / 60 / 24;
        return (days == 0 ? "" : days + " Tage, ") + (hours == 0 ? "" : hours + " Stunden, ") + (minutes == 0 ? "" : minutes + " Minuten, ") + seconds + " Sekunden";
    }

    public static String renderTime(long timestamp) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        return sdfDate.format(new Date(timestamp * 1000));
    }

    private void registerCommand(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }

    private void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(this, listener);
    }

    public void loadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return config;
    }

    private void saveDefaultConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                ByteStreams.copy(getResourceAsStream("bungeeconfig.yml"), new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
