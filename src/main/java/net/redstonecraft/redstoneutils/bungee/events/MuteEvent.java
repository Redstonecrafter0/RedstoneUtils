package net.redstonecraft.redstoneutils.bungee.events;

import net.md_5.bungee.api.plugin.Event;
import net.redstonecraft.redstoneutils.bungee.obj.Mute;

public class MuteEvent extends Event {

    public final Mute mute;

    public MuteEvent(Mute mute) {
        this.mute = mute;
    }

}
