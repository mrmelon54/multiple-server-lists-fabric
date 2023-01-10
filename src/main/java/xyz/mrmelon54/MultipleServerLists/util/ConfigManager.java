package xyz.mrmelon54.MultipleServerLists.util;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.api.SyntaxError;
import io.github.cottonmc.jankson.JanksonFactory;
import net.minecraft.client.MinecraftClient;
import xyz.mrmelon54.MultipleServerLists.client.MultipleServerListsClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private final MinecraftClient mc;
    private File serversWrapperFolder;
    private File configFile;
    public Config config = new Config();

    public ConfigManager(MinecraftClient mc) throws IOException, SyntaxError {
        this.mc = mc;
        try {
            load();
        } catch (IOException | SyntaxError e) {
            if (e instanceof FileNotFoundException) {
                try {
                    save();
                } catch (IOException ex) {
                    MultipleServerListsClient.LOGGER.error("Failed to save example config file:", e);
                }
            } else {
                throw e;
            }
        }
    }

    public void load() throws SyntaxError, IOException {
        if (makeSureFile()) {
            Jankson j = JanksonFactory.createJankson();
            config = j.fromJson(j.load(configFile), Config.class);
        }
    }

    public void save() throws IOException {
        if (makeSureFile()) {
            try (FileWriter in = new FileWriter(configFile)) {
                JanksonFactory.createJankson().toJson(config).toJson(in, JsonGrammar.JSON5, 0);
            }
        }
    }

    private boolean makeSureFile() {
        if (makeSureFolderExists()) {
            if (configFile == null) configFile = new File(serversWrapperFolder, "msl.json5");
            return true;
        }
        return false;
    }

    private boolean makeSureFolderExists() {
        if (this.serversWrapperFolder == null) this.serversWrapperFolder = new File(mc.runDirectory, "s760");
        return serversWrapperFolder.exists() || serversWrapperFolder.mkdirs();
    }

    public static class Config {
        public boolean ShowTabs = true;
    }
}
