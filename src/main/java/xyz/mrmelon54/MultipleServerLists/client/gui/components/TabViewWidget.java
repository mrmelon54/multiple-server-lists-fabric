package xyz.mrmelon54.MultipleServerLists.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabViewWidget extends AlwaysSelectedEntryListWidget<TabViewWidget.TabWidget> {
    public List<CustomFileServerList> serverLists = new ArrayList<>();
    private int scrollX = 30;
    private int totalWidth;

    public TabViewWidget(MinecraftClient mc, int width, int top) {
        super(mc, width, 20, top, top + 20, 20);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        setRenderSelection(true);
        serverLists.add(new CustomFileServerList(mc, 1));
        serverLists.add(new CustomFileServerList(mc, 2));
        serverLists.add(new CustomFileServerList(mc, 3));
        serverLists.add(new CustomFileServerList(mc, 4));
        serverLists.add(new CustomFileServerList(mc, 5));
        serverLists.add(new CustomFileServerList(mc, 6));
        refresh();
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return super.hoveredElement(mouseX, mouseY);
    }

    public void refresh() {
        List<TabWidget> c = this.children();
        c.clear();
        int totalWidth = 0;
        for (CustomFileServerList serverList : serverLists) {
            TabWidget t = new TabWidget(-200, serverList, button -> System.out.println("change to tab " + serverList.getName()));
            totalWidth += t.getActualWidth();
            c.add(t);
        }
        this.totalWidth = totalWidth;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int scrollX = getActualScrollAmount();
        int w = -scrollX;
        List<TabWidget> children = children();
        for (int i = 0; i < children.size(); i++) {
            TabWidget child = children.get(i);
            int a = child.getActualWidth();
            if (w <= width && w + a >= 0) {
                boolean hovered = mouseX >= w && mouseX < w + a && mouseY >= top && mouseY < bottom;
                child.render(matrices, i, this.top, w, a, 20, mouseX, mouseY, hovered, delta);
            }
            w += a;
        }
    }

    private int getActualScrollAmount() {
        return Math.min(this.scrollX, width - totalWidth);
    }

    public class TabWidget extends AlwaysSelectedEntryListWidget.Entry<TabWidget> {
        private static final Identifier SERVER_TABS_TEXTURE = new Identifier("multiple-server-lists", "textures/gui/server-tabs.png");

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
            int i = hovered ? 1 : 0;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            drawTexture(matrices, x, y, 0, i * 20, w / 2, entryHeight);
            drawTexture(matrices, x + w / 2, y, 200 - w / 2, i * 20, w / 2, entryHeight);
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
