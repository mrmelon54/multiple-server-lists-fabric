package xyz.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.network.ServerInfo;

import java.util.List;

public interface ServerListDuckProvider {
    List<ServerInfo> getServers();

    List<ServerInfo> getHiddenServers();
}
