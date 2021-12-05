package net.onpointcoding.multipleserverlists.client.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.text.TranslatableText;

public class UninstallInfoGui extends LightweightGuiDescription {
    private Runnable closeInfoCallback;

    public UninstallInfoGui() {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 100);
        root.setInsets(Insets.ROOT_PANEL);
        root.setBackgroundPainter(BackgroundPainter.VANILLA);
        root.setHost(this);

        WLabel label = new WLabel(new TranslatableText("multiple-server-lists.screen.safe-uninstall.title"));
        root.add(label, 0, 0, root.getWidth(), 18);

        WText text = new WText(new TranslatableText("multiple-server-lists.screen.safe-uninstall.info"));
        root.add(text, 0, 20, root.getWidth(), root.getHeight() - 40);

        WButton okBtn = new WButton(new TranslatableText("multiple-server-lists.screen.safe-uninstall.ok"));
        okBtn.setOnClick(() -> {
            if (closeInfoCallback != null)
                closeInfoCallback.run();
        });
        root.add(okBtn, 0, root.getHeight() - 20, root.getWidth(), 18);

        root.validate(this);
    }

    public void setCloseInfoCallback(Runnable callback) {
        closeInfoCallback = callback;
    }
}
