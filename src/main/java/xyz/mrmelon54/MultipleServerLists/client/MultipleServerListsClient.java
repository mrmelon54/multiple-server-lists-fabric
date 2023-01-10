package xyz.mrmelon54.MultipleServerLists.client;

import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.mrmelon54.MultipleServerLists.duck.MultiplayerScreenDuckProvider;
import xyz.mrmelon54.MultipleServerLists.util.ConfigManager;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class MultipleServerListsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("xyz.mrmelon54.multiple-server-lists");
    private static MultipleServerListsClient instance;
    private static final Pattern pattern = Pattern.compile("servers([0-9]+)\\.dat$");
    private MultiplayerScreenDuckProvider multiplayerScreenDuck;
    public ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        instance = this;
        try {
            configManager = new ConfigManager(MinecraftClient.getInstance());
        } catch (IOException | SyntaxError e) {
            MultipleServerListsClient.LOGGER.error("Failed to load config file:", e);
        }
    }

    public static MultipleServerListsClient getInstance() {
        return instance;
    }

    public List<Integer> getTabList() {
        List<Integer> availableTabs = new ArrayList<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        File serversWrapperFolder = new File(mc.runDirectory, "s760");
        if (serversWrapperFolder.exists()) {
            Predicate<String> patternPredicate = pattern.asMatchPredicate();
            File[] files = serversWrapperFolder.listFiles((dir, name) -> patternPredicate.test(name));
            if (files != null) {
                for (File file : files) {
                    final Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find()) {
                        try {
                            availableTabs.add(Integer.parseInt(matcher.group(1)));
                        } catch (NumberFormatException ignored) {
                            System.out.println("The number " + matcher.group(1) + " was unable to be read so the file was not included in the merged vanilla server lists");
                            System.out.println("If this resulted in missing servers then please inform the developer via Discord, CurseForge or GitHub");
                        }
                    }
                }
            }

            Collections.sort(availableTabs);
        }
        return availableTabs;
    }

    public List<CustomFileServerList> getTabServerList() {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Integer> tabList = getTabList();
        return tabList.stream().map(n -> new CustomFileServerList(mc, n)).collect(Collectors.toList());
    }

    @SuppressWarnings("ConstantConditions")
    public void safeUninstallForVanilla() {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Integer> tabList = getTabList();
        ServerList mainTab = getServerListForTab(mc, 0);
        mainTab.loadFile();
        for (int a : tabList) {
            ServerList currentTab = getServerListForTab(mc, a);
            currentTab.loadFile();
            for (int i = 0; i < currentTab.size(); i++) mainTab.add(currentTab.get(i), false);
        }
        mainTab.saveFile();

        // wait until after saving
        for (int a : tabList)
            if (getServerListForTab(mc, a) instanceof CustomFileServerList customFileServerList)
                customFileServerList.deleteFile();
    }

    public ServerList getServerListForTab(MinecraftClient mc, int tab) {
        if (tab < 0) return null;
        return tab == 0 ? new ServerList(mc) : new CustomFileServerList(mc, tab);
    }

    public void setMultiplayerScreen(MultiplayerScreenDuckProvider duck) {
        multiplayerScreenDuck = duck;
    }

    public void setTab(int n) {
        if (multiplayerScreenDuck != null) multiplayerScreenDuck.setCurrentTab(n);
    }

    public int getTab() {
        return multiplayerScreenDuck != null ? multiplayerScreenDuck.getCurrentTab() : 0;
    }
}
