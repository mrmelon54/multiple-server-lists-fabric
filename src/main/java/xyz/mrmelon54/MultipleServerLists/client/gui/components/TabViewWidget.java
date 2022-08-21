package xyz.mrmelon54.MultipleServerLists.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

import java.util.ArrayList;
import java.util.List;

public class TabViewWidget extends AlwaysSelectedEntryListWidget<TabViewWidget.TabWidget> {
    private static final Identifier SERVER_TABS_TEXTURE = new Identifier("multiple-server-lists", "textures/gui/server-tabs.png");

    public List<CustomFileServerList> serverLists = new ArrayList<>();
    private int scrollX = 0;
    private int totalWidth;
    private final ButtonWidget scrollLeft;
    private final ButtonWidget scrollRight;
    private final ButtonWidget scrollDropdown;

    public TabViewWidget(MinecraftClient mc, int width, int top) {
        super(mc, width, 20, top, top + 20, 20);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        setRenderSelection(true);
        this.scrollLeft = new TexturedButtonWidget(0, top, 20, 20, 0, 80, 20, SERVER_TABS_TEXTURE, button -> scrollX -= 80);
        this.scrollRight = new TexturedButtonWidget(width - 40, top, 20, 20, 20, 80, 20, SERVER_TABS_TEXTURE, button -> scrollX += 80);
        this.scrollDropdown = new TexturedButtonWidget(width - 20, top, 20, 20, 40, 80, 20, SERVER_TABS_TEXTURE, button -> System.out.println("Showing tab dropdown"));
        serverLists.add(new CustomFileServerList(mc, 1));
        serverLists.add(new CustomFileServerList(mc, 2));
        serverLists.add(new CustomFileServerList(mc, 3));
        serverLists.add(new CustomFileServerList(mc, 4));
        serverLists.add(new CustomFileServerList(mc, 5));
        serverLists.add(new CustomFileServerList(mc, 6));
        serverLists.add(new CustomFileServerList(mc, 7));
        serverLists.add(new CustomFileServerList(mc, 8));
        serverLists.add(new CustomFileServerList(mc, 9));
        serverLists.add(new CustomFileServerList(mc, 10));
        serverLists.add(new CustomFileServerList(mc, 11));
        serverLists.add(new CustomFileServerList(mc, 12));
        refresh();
    }

    public void refresh() {
        List<TabWidget> c = this.children();
        c.clear();
        int totalWidth = 0;
        for (CustomFileServerList serverList : serverLists) {
            TabWidget t = new TabWidget(-200, serverList, button -> System.out.println("change to tab " + button.serverList.getName()));
            totalWidth += t.getActualWidth();
            c.add(t);
        }
        this.totalWidth = totalWidth;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        boolean needsScroll = totalWidth >= width;
        int scrollX = getActualScrollAmount(needsScroll ? 60 : 0);
        int sw = width + (needsScroll ? -40 : 0);
        int xx = needsScroll ? 20 : 0;
        int w = xx - scrollX;
        List<TabWidget> children = children();
        for (int i = 0; i < children.size(); i++) {
            TabWidget child = children.get(i);
            int a = child.getActualWidth();
            if (w <= sw && w + a >= 0) {
                boolean hovered = mouseX >= xx && mouseX < sw && mouseX >= w && mouseX < w + a && mouseY >= top && mouseY < bottom;
                child.render(matrices, i, this.top, w, sw - w + a, 20, mouseX, mouseY, hovered, delta);
            }
            w += a;
        }
        if (needsScroll) {
            matrices.push();
            matrices.translate(0, 0, 50);
            RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
            if (scrollX > 0) this.scrollLeft.render(matrices, mouseX, mouseY, delta);
            else drawTexture(matrices, 0, top, 0, 60, 20, 20, 256, 256);
            if (scrollX < totalWidth - sw) this.scrollRight.render(matrices, mouseX, mouseY, delta);
            else drawTexture(matrices, sw, top, 20, 60, 20, 20, 256, 256);
            this.scrollDropdown.render(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean needsScroll = totalWidth >= width;
        int sw = width + (needsScroll ? -40 : 0);
        int scrollX = getActualScrollAmount(sw);
        int w = needsScroll ? 20 - scrollX : 0;
        if (needsScroll) {
            if (this.scrollLeft.isMouseOver(mouseX, mouseY))
                return this.scrollLeft.mouseClicked(mouseX, mouseY, button);
            if (this.scrollRight.isMouseOver(mouseX, mouseY))
                return this.scrollRight.mouseClicked(mouseX, mouseY, button);
            if (this.scrollDropdown.isMouseOver(mouseX, mouseY))
                return this.scrollDropdown.mouseClicked(mouseX, mouseY, button);
        }
        List<TabWidget> children = children();
        for (TabWidget child : children) {
            int a = child.getActualWidth();
            if (w <= sw && w + a >= 0 && mouseX >= w && mouseX < w + a && mouseY >= top && mouseY < bottom)
                return child.mouseClicked(mouseX, mouseY, button);
            w += a;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getActualScrollAmount(int sw) {
        if (scrollX < 0) scrollX = 0;
        if (scrollX >= totalWidth - sw) scrollX = totalWidth - sw;
        return scrollX;
    }

    public class TabWidget extends AlwaysSelectedEntryListWidget.Entry<TabWidget> {
        private final int width;
        private final CustomFileServerList serverList;
        private final Text message;
        private final PressAction onPress;

        public TabWidget(int width, CustomFileServerList serverList, PressAction onPress) {
            this.width = width;
            this.serverList = serverList;
            message = Text.of(serverList.getName());
            this.onPress = onPress;
        }

        public CustomFileServerList getServerList() {
            return serverList;
        }

        @Override
        public Text getNarration() {
            return message;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            System.out.println("mouseClicked");
            onPress.onPress(this);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            System.out.println("mouseReleased");
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            TextRenderer textRenderer = minecraftClient.textRenderer;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            int w = getActualWidth();
            int i = hovered ? 2 : 1;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            drawTexture(matrices, x, y, 0, i * 20, Math.min(entryWidth, w / 2), entryHeight);
            drawTexture(matrices, x + w / 2, y, 200 - w / 2, i * 20, Math.min(entryWidth - w / 2, w / 2), entryHeight);
            drawCenteredText(matrices, textRenderer, message, x + w / 2, y + (height - 8) / 2, 0xffffffff);
        }

        public int getActualWidth() {
            return width < 0 ? Math.min(-width, client.textRenderer.getWidth(message) + 12) : width;
        }

        @Environment(EnvType.CLIENT)
        public interface PressAction {
            void onPress(TabWidget button);
        }
    }
}
