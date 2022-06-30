package xyz.mrmelon54.MultipleServerLists.util;

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
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.mrmelon54.MultipleServerLists.duck.ServerListDuckProvider;

import java.io.File;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomFileServerList extends ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private File serversWrapperFolder;
    private String listName;
    private final int pageIndex;

    public CustomFileServerList(MinecraftClient client, int pageIndex) {
        super(client);
        this.pageIndex = pageIndex;
        this.listName = "Page " + pageIndex;
        this.loadFile();
    }

    List<ServerInfo> getInternalServers() {
        if (this instanceof ServerListDuckProvider duck) return duck.getServers();
        return Lists.newArrayList();
    }

    List<ServerInfo> getInternalHiddenServers() {
        if (this instanceof ServerListDuckProvider duck) return duck.getHiddenServers();
        return Lists.newArrayList();
    }

    public boolean makeSureFolderExists() {
        if (this.serversWrapperFolder == null)
            this.serversWrapperFolder = new File(MinecraftClient.getInstance().runDirectory, "s760");
        return serversWrapperFolder.exists() || serversWrapperFolder.mkdirs();
    }

    @Override
    public void loadFile() {
        try {
            getInternalServers().clear();
            getInternalHiddenServers().clear();

            if (makeSureFolderExists()) {
                NbtCompound nbtCompound = NbtIo.read(new File(serversWrapperFolder, "servers" + pageIndex + ".dat"));
                if (nbtCompound == null)
                    return;

                if (nbtCompound.contains("name", NbtType.STRING))
                    listName = nbtCompound.getString("name");
                NbtList nbtList = nbtCompound.getList("servers", NbtType.COMPOUND);

                for (int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound c = nbtList.getCompound(i);
                    ServerInfo serverInfo = ServerInfo.fromNbt(c);
                    if (c.getBoolean("hidden")) getInternalHiddenServers().add(serverInfo);
                    else getInternalServers().add(serverInfo);
                }
            }
        } catch (Exception var4) {
            LOGGER.error("Couldn't load server list", var4);
        }
    }

    @Override
    public void saveFile() {
        try {
            if (makeSureFolderExists()) {
                NbtList nbtList = new NbtList();
                for (ServerInfo serverInfo : getInternalServers()) {
                    NbtCompound c = serverInfo.toNbt();
                    c.putBoolean("hidden", false);
                    nbtList.add(c);
                }
                for (ServerInfo serverInfo : getInternalHiddenServers()) {
                    NbtCompound c = serverInfo.toNbt();
                    c.putBoolean("hidden", true);
                    nbtList.add(c);
                }

                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("name", listName);
                nbtCompound.put("servers", nbtList);

                String n = "servers" + pageIndex;
                File file = File.createTempFile(n, ".dat", this.serversWrapperFolder);
                NbtIo.write(nbtCompound, file);
                File file2 = new File(this.serversWrapperFolder, n + ".dat_old");
                File file3 = new File(this.serversWrapperFolder, n + ".dat");
                Util.backupAndReplace(file3, file, file2);
            }
        } catch (Exception var6) {
            LOGGER.error("Couldn't save server list", var6);
        }
    }

    public void deleteFile() {
        try {
            if (makeSureFolderExists()) {
                File file3 = new File(this.serversWrapperFolder, "servers" + pageIndex + ".dat");
                file3.delete();
            }
        } catch (Exception var6) {
            LOGGER.error("Couldn't remove the server list", var6);
        }
    }

    public void setName(String value) {
        listName = value;
    }

    public String getName() {
        return listName;
    }
}
