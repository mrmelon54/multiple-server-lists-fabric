package net.onpointcoding.multipleserverlists.client.screen;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class BaseClosableScreen extends CottonClientScreen {
    private final Screen parent;

    public BaseClosableScreen(GuiDescription description, Screen parent) {
        super(description);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (this.client != null)
            this.client.setScreen(parent);
    }
}
