package xyz.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.option.ServerList;

public interface MultiplayerScreenDuckProvider {
    ServerList getServerListForTab(int tab);

    int getCurrentTab();

    void setCurrentTab(int currentTab);
}
