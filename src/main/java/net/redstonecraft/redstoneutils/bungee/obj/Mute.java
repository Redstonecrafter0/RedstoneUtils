package net.redstonecraft.redstoneutils.bungee.obj;

public class Mute {

    public final User user;
    public final User mutedBy;
    public final long since;
    public final long until;
    public final String reason;
    public final boolean mutedByConsole;

    public Mute(User user, User mutedBy, long since, long until, boolean mutedByConsole, String reason) {
        this.user = user;
        this.mutedBy = mutedBy;
        this.since = since;
        this.until = until;
        this.reason = reason;
        this.mutedByConsole = mutedByConsole;
    }

}
