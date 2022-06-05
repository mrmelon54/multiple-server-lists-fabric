package xyz.mrmelon54.MultipleServerLists.client.screen;

import io.github.cottonmc.cotton.gui.GuiDescription;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class MultipleServerListsSettingsScreen extends BaseClosableScreen {
    public MultipleServerListsSettingsScreen(GuiDescription description, Screen parent) {
        super(description, parent);
    }
}
