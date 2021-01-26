package net.redstonecraft.redstoneutils.bungee.processors;

import net.md_5.bungee.api.ProxyServer;
import net.redstonecraft.redstoneapi.ipc.interfaces.ServerProcessor;
import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.ipc.response.ResponseStatus;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneutils.bungee.managers.VanishManager;
import net.redstonecraft.redstoneutils.enums.Packets;
import net.redstonecraft.redstoneutils.enums.PlayerVanishStateChangeStatus;

import java.util.UUID;

public class PlayerVanishProcessor extends ServerProcessor {

    private final VanishManager vanishManager;

    public PlayerVanishProcessor(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Override
    public Response onProcess(JSONObject jsonObject) {
        PlayerVanishStateChangeStatus changeStatus = PlayerVanishStateChangeStatus.valueOf(jsonObject.getString("status"));
        UUID uuid = UUID.fromString(jsonObject.getString("uuid"));
        switch (changeStatus) {
            case ADD:
                vanishManager.vanish(ProxyServer.getInstance().getPlayer(uuid));
                break;
            case REMOVE:
                vanishManager.unvanish(ProxyServer.getInstance().getPlayer(uuid));
                break;
            default:
                return new Response(ResponseStatus.ERROR, new JSONObject());
        }
        return new Response(ResponseStatus.SUCCESS, new JSONObject());
    }

    @Override
    public String getPacketName() {
        return Packets.PLAYERVANISHSTATECHANGE.packetName;
    }
}
