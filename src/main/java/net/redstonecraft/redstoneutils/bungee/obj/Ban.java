package net.redstonecraft.redstoneutils.bungee.obj;

public class Ban {

    public final User bannedPlayer;
    public final User bannedBy;
    public final Long since;
    public final Long until;
    public final boolean unbanned;
    public final boolean bannedByConsole;
    public final boolean unbannedByConsole;
    public final boolean unbannedAuto;
    public final boolean isPermanent;
    public final User unbannedBy;
    public final String reason;

    public Ban(User bannedPlayer, User bannedBy, Long since, Long until, boolean unbanned, String reason, User unbannedBy, String unbannedByS) {
        this.bannedPlayer = bannedPlayer;
        this.bannedBy = bannedBy;
        this.since = since;
        this.until = until;
        this.unbanned = unbanned;
        this.bannedByConsole = bannedBy == null;
        this.unbannedByConsole = unbannedByS.equals("CONSOLE");
        this.unbannedAuto = unbannedByS.equals("AUTO");
        this.unbannedBy = unbannedBy;
        this.reason = reason;
        this.isPermanent = until == 0;
    }

}
