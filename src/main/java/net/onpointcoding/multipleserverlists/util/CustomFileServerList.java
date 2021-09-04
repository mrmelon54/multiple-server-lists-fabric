package net.onpointcoding.multipleserverlists.util;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomFileServerList extends ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private File serversWrapperFolder;
    private List<ServerInfo> customServers;
    private final int pageIndex;

    public CustomFileServerList(MinecraftClient client, int pageIndex) {
        super(client);
        this.pageIndex = pageIndex;
        this.loadFile();
    }

    @Override
    public void loadFile() {
        try {
            if (this.customServers == null)
                this.customServers = Lists.newArrayList();
            this.customServers.clear();
            if (this.serversWrapperFolder == null)
                this.serversWrapperFolder = new File(MinecraftClient.getInstance().runDirectory, "s760");
            if (serversWrapperFolder.exists() || serversWrapperFolder.mkdirs()) {
                NbtCompound nbtCompound = NbtIo.read(new File(serversWrapperFolder, "servers" + pageIndex + ".dat"));
                if (nbtCompound == null) {
                    return;
                }

                NbtList nbtList = nbtCompound.getList("servers", 10);

                for (int i = 0; i < nbtList.size(); ++i) {
                    this.customServers.add(ServerInfo.fromNbt(nbtList.getCompound(i)));
                }
            }
        } catch (Exception var4) {
            LOGGER.error("Couldn't load server list", var4);
        }

    }

    @Override
    public void saveFile() {
        try {
            if (this.customServers == null)
                this.customServers = Lists.newArrayList();
            if (this.serversWrapperFolder == null)
                this.serversWrapperFolder = new File(MinecraftClient.getInstance().runDirectory, "s760");

            NbtList nbtList = new NbtList();
            for (ServerInfo serverInfo : this.customServers) nbtList.add(serverInfo.toNbt());

            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("servers", nbtList);
            File file = File.createTempFile("servers" + pageIndex, ".dat", this.serversWrapperFolder);
            NbtIo.write(nbtCompound, file);
            File file2 = new File(this.serversWrapperFolder, "servers" + pageIndex + ".dat_old");
            File file3 = new File(this.serversWrapperFolder, "servers" + pageIndex + ".dat");
            Util.backupAndReplace(file3, file, file2);
        } catch (Exception var6) {
            LOGGER.error("Couldn't save server list", var6);
        }

    }

    public ServerInfo get(int index) {
        return this.customServers.get(index);
    }

    public void remove(ServerInfo serverInfo) {
        this.customServers.remove(serverInfo);
    }

    public void add(ServerInfo serverInfo) {
        this.customServers.add(serverInfo);
    }

    public int size() {
        return this.customServers.size();
    }

    public void swapEntries(int index1, int index2) {
        ServerInfo serverInfo = this.get(index1);
        this.customServers.set(index1, this.get(index2));
        this.customServers.set(index2, serverInfo);
        this.saveFile();
    }

    public void set(int index, ServerInfo serverInfo) {
        this.customServers.set(index, serverInfo);
    }

    public static void updateServerListEntry(ServerInfo e) {
        net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
        serverList.loadFile();

        for (int i = 0; i < serverList.size(); ++i) {
            ServerInfo serverInfo = serverList.get(i);
            if (serverInfo.name.equals(e.name) && serverInfo.address.equals(e.address)) {
                serverList.set(i, e);
                break;
            }
        }

        serverList.saveFile();
    }
}
