package xyz.mrmelon54.MultipleServerLists.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.mrmelon54.MultipleServerLists.duck.ServerEntryDuckProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin implements ServerEntryDuckProvider {
    @Unique
    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");

    @Override
    public void extendedRender(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable) {
        RenderSystem.setShaderTexture(0, SERVER_SELECTION_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int t = x + entryWidth;
        int u = isScrollable ? t + 6 : t;
        int v = mouseX - x;
        if (hovered) {
            // Move server left arrow
            if (v < 0 && v > -16)
                DrawableHelper.drawTexture(matrices, x - 16, y, 16, 32, 32, 32, 16, 32, 256, 256);
            else
                DrawableHelper.drawTexture(matrices, x - 16, y, 16, 32, 32, 0, 16, 32, 256, 256);

            // Move server right arrow
            if (v - entryWidth > 0 && v - entryWidth < 16)
                DrawableHelper.drawTexture(matrices, u, y, 16, 32, 16, 32, 16, 32, 256, 256);
            else
                DrawableHelper.drawTexture(matrices, u, y, 16, 32, 16, 0, 16, 32, 256, 256);
        }
    }
}
