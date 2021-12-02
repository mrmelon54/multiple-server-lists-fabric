package net.onpointcoding.multipleserverlists.client.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.onpointcoding.multipleserverlists.client.MultipleServerListsClient;
import net.onpointcoding.multipleserverlists.client.screen.UninstallInfoScreen;

@Environment(EnvType.CLIENT)
public class MultipleServerListsSettingsGui extends LightweightGuiDescription {

    public MultipleServerListsSettingsGui() {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 50);
        root.setInsets(Insets.ROOT_PANEL);
        root.setBackgroundPainter(BackgroundPainter.VANILLA);
        root.setHost(this);

        MultipleServerListsClient multipleServerListsClient = MultipleServerListsClient.getInstance();

        WLabel label = new WLabel(new TranslatableText("multiple-server-lists.screen.settings.title"));
        root.add(label, 0, 0, root.getWidth(), 18);

        WButton resetBtn = new WButton(new TranslatableText("multiple-server-lists.screen.settings.safe-uninstall"));
        resetBtn.setOnClick(() -> {
            multipleServerListsClient.safeUninstallForVanilla();
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.setScreen(new UninstallInfoScreen(new UninstallInfoGui(), mc.currentScreen));
        });
        root.add(resetBtn, 0, 20, root.getWidth(), 18);

        root.validate(this);
    }
}
