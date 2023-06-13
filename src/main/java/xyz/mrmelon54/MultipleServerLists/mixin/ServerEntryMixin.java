package xyz.mrmelon54.MultipleServerLists.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.mrmelon54.MultipleServerLists.duck.ServerEntryDuckProvider;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin implements ServerEntryDuckProvider {
    @Unique
    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");

    @Override
    public void extendedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable) {
        int t = x + entryWidth;
        int u = isScrollable ? t + 6 : t;
        int v = mouseX - x;

        if (hovered) {
            // Move server left arrow
            if (v < 0 && v > -16)
                context.drawTexture(SERVER_SELECTION_TEXTURE, x - 16, y, 16, 32, 32, 32, 16, 32, 256, 256);
            else
                context.drawTexture(SERVER_SELECTION_TEXTURE, x - 16, y, 16, 32, 32, 0, 16, 32, 256, 256);

            // Move server right arrow
            if (v - entryWidth > 0 && v - entryWidth < 16)
                context.drawTexture(SERVER_SELECTION_TEXTURE, u, y, 16, 32, 16, 32, 16, 32, 256, 256);
            else
                context.drawTexture(SERVER_SELECTION_TEXTURE, u, y, 16, 32, 16, 0, 16, 32, 256, 256);
        }
    }
}
