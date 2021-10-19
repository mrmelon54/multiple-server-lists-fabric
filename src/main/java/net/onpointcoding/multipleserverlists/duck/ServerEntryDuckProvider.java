package net.onpointcoding.multipleserverlists.duck;

import net.minecraft.client.util.math.MatrixStack;

public interface ServerEntryDuckProvider {
    void extendedRender(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable);
}
