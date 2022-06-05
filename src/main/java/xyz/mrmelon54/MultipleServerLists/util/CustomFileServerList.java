package xyz.mrmelon54.MultipleServerLists.util;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
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
    private String listName;
    private List<ServerInfo> customServers;
    private final int pageIndex;

    public CustomFileServerList(MinecraftClient client, int pageIndex) {
        super(client);
        this.pageIndex = pageIndex;
        this.listName = "Page " + pageIndex;
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
                if (nbtCompound == null)
                    return;

                if (nbtCompound.contains("name", NbtType.STRING))
                    listName = nbtCompound.getString("name");
                NbtList nbtList = nbtCompound.getList("servers", NbtType.COMPOUND);

                for (int i = 0; i < nbtList.size(); ++i)
                    this.customServers.add(ServerInfo.fromNbt(nbtList.getCompound(i)));
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
            nbtCompound.putString("name", listName);
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

    public void deleteFile() {
        try {
            if (this.customServers == null)
                this.customServers = Lists.newArrayList();
            if (this.serversWrapperFolder == null)
                this.serversWrapperFolder = new File(MinecraftClient.getInstance().runDirectory, "s760");

            File file3 = new File(this.serversWrapperFolder, "servers" + pageIndex + ".dat");
            file3.delete();
        } catch (Exception var6) {
            LOGGER.error("Couldn't remove the server list", var6);
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

    public void add(int index, ServerInfo serverInfo) {
        this.customServers.add(index, serverInfo);
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

    public String getName() {
        return listName;
    }

    public void setName(String value) {
        listName = value;
    }
}
