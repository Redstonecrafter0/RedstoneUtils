package net.redstonecraft.redstoneutils.bungee.obj;

import net.redstonecraft.redstoneutils.enums.LabyModActionType;

public class LabyModAction {

    public final String displayName;
    public final LabyModActionType type;
    public final String value;

    public LabyModAction(String displayName, LabyModActionType type, String value) {
        this.displayName = displayName;
        this.type = type;
        this.value = value;
    }

}
