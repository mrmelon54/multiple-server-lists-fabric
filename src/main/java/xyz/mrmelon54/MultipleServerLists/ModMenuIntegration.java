package xyz.mrmelon54.MultipleServerLists;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.mrmelon54.MultipleServerLists.client.gui.MultipleServerListsSettingsGui;
import xyz.mrmelon54.MultipleServerLists.client.screen.MultipleServerListsSettingsScreen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new MultipleServerListsSettingsScreen(new MultipleServerListsSettingsGui(), parent);
    }
}
