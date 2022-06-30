package xyz.mrmelon54.MultipleServerLists.mixin;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.mrmelon54.MultipleServerLists.duck.ServerListDuckProvider;

import java.util.List;

@Mixin(ServerList.class)
public class ServerListMixin implements ServerListDuckProvider {
    @Shadow
    @Final
    private List<ServerInfo> servers;

    @Shadow
    @Final
    private List<ServerInfo> hiddenServers;

    @Override
    public List<ServerInfo> getServers() {
        return this.servers;
    }

    @Override
    public List<ServerInfo> getHiddenServers() {
        return this.hiddenServers;
    }
}
