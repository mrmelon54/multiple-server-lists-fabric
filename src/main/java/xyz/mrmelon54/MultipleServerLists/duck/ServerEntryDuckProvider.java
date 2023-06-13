package xyz.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.gui.DrawContext;

public interface ServerEntryDuckProvider {
    void extendedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable);
}
