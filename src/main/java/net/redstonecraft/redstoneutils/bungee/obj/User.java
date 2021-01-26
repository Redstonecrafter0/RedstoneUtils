package net.redstonecraft.redstoneutils.bungee.obj;

import java.util.UUID;

public class User {

    public final UUID uuid;
    public final String name;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean equals(User user) {
        return uuid.equals(user.uuid);
    }

}
