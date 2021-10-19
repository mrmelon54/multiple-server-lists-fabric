package net.onpointcoding.multipleserverlists;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.onpointcoding.multipleserverlists.client.gui.MultipleServerListsSettingsGui;
import net.onpointcoding.multipleserverlists.client.screen.MultipleServerListsSettingsScreen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new MultipleServerListsSettingsScreen(new MultipleServerListsSettingsGui(), parent);
    }
}
