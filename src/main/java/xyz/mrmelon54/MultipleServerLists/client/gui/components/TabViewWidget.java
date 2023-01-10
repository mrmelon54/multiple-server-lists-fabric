package xyz.mrmelon54.MultipleServerLists.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mrmelon54.MultipleServerLists.client.MultipleServerListsClient;
import xyz.mrmelon54.MultipleServerLists.client.screen.EditListNameScreen;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

import java.util.List;

public class TabViewWidget extends AlwaysSelectedEntryListWidget<TabViewWidget.TabWidget> {
    private static final Identifier SERVER_TABS_TEXTURE = new Identifier("multiple-server-lists", "textures/gui/server-tabs.png");
    private final MultipleServerListsClient multipleServerListsClient;
    private final ItemStack featherStack;
    public List<CustomFileServerList> serverLists;
    private int scrollX = 0;
    private int totalWidth;
    private final ButtonWidget scrollLeft;
    private final ButtonWidget scrollRight;
    private final ButtonWidget scrollDropdown;
    private final ButtonWidget editServerListNameButton;

    public TabViewWidget(MinecraftClient mc, Screen screen, int width, int top) {
        super(mc, width, 20, top, top + 20, 20);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        setRenderSelection(true);
        multipleServerListsClient = MultipleServerListsClient.getInstance();
        serverLists = multipleServerListsClient.getTabServerList();
        featherStack = new ItemStack(Items.FEATHER);

        this.scrollLeft = new TexturedButtonWidget(0, top, 20, 20, 0, 80, 20, SERVER_TABS_TEXTURE, button -> {
        });
        this.scrollRight = new TexturedButtonWidget(width - 60, top, 20, 20, 20, 80, 20, SERVER_TABS_TEXTURE, button -> {
        });
        this.scrollDropdown = new TexturedButtonWidget(width - 40, top, 20, 20, 40, 80, 20, SERVER_TABS_TEXTURE, button -> {
        });
        this.editServerListNameButton = new ButtonWidget(width - 20, top, 20, 20, Text.literal(""), (button) -> {
            int tab = multipleServerListsClient.getTab() - 1;
            if (tab < 0 || tab > serverLists.size()) return;
            CustomFileServerList currentServerList = serverLists.get(tab);
            EditListNameScreen editListNameScreen = new EditListNameScreen(Text.translatable("multiple-server-lists.screen.edit-list-name.title"), screen, currentServerList);
            if (this.client != null)
                this.client.setScreen(editListNameScreen);
        });

        refresh();
    }

    public void refresh() {
        List<TabWidget> c = this.children();
        c.clear();
        int totalWidth = 0;
        TabWidget t = new TabWidget(-200, null, button -> multipleServerListsClient.setTab(0));
        totalWidth += t.getActualWidth();
        c.add(t);
        for (int i = 0; i < serverLists.size(); i++) {
            CustomFileServerList serverList = serverLists.get(i);
            final int j = i + 1;
            t = new TabWidget(-200, serverList, button -> multipleServerListsClient.setTab(j));
            totalWidth += t.getActualWidth();
            c.add(t);
        }
        this.totalWidth = totalWidth;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        boolean needsScroll = totalWidth >= width;
        int scrollX = getActualScrollAmount(needsScroll ? 80 : 0);
        int sw = width + (needsScroll ? -60 : 0);
        int xx = needsScroll ? 20 : 0;
        int w = xx - scrollX;
        int selectedTab = multipleServerListsClient.getTab();
        List<TabWidget> children = children();
        for (int i = 0; i < children.size(); i++) {
            TabWidget child = children.get(i);
            int a = child.getActualWidth();
            if (w <= sw && w + a >= 0) {
                boolean hovered = mouseX >= xx && mouseX < sw && mouseX >= w && mouseX < w + a && mouseY >= top && mouseY < bottom;
                child.selected = selectedTab == i;
                child.render(matrices, i, this.top, w, sw - w + a, 20, mouseX, mouseY, hovered, delta);
            }
            w += a;
        }
        matrices.push();
        matrices.translate(0, 0, 50);
        if (needsScroll) {
            if (scrollX > 0) this.scrollLeft.render(matrices, mouseX, mouseY, delta);
            else {
                RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
                drawTexture(matrices, 0, top, 0, 60, 20, 20, 256, 256);
            }
            if (scrollX < totalWidth - sw) this.scrollRight.render(matrices, mouseX, mouseY, delta);
            else {
                RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
                drawTexture(matrices, width - 60, top, 20, 60, 20, 20, 256, 256);
            }
            this.scrollDropdown.render(matrices, mouseX, mouseY, delta);
        }
        if (selectedTab > 0) this.editServerListNameButton.render(matrices, mouseX, mouseY, delta);
        else {
            RenderSystem.setShaderTexture(0, ButtonWidget.WIDGETS_TEXTURE);
            this.drawTexture(matrices, width - 20, top, 0, 46, 10, 20);
            this.drawTexture(matrices, width - 10, top, 200 - 10, 46, 10, 20);
        }
        if (featherStack != null)
            this.client.getItemRenderer().renderInGui(featherStack, width - 18, top + 2);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean needsScroll = totalWidth >= width;
        int sw = width + (needsScroll ? -80 : 0);
        int scrollX = getActualScrollAmount(sw);
        int w = needsScroll ? 20 - scrollX : 0;
        if (needsScroll) {
            int scrollMoveAmount = 80;
            if (this.scrollLeft.isMouseOver(mouseX, mouseY)) {
                this.scrollX -= scrollMoveAmount;
                getActualScrollAmount(sw);
                return true;
            }
            if (this.scrollRight.isMouseOver(mouseX, mouseY)) {
                this.scrollX += scrollMoveAmount;
                getActualScrollAmount(sw);
                return true;
            }
            if (this.editServerListNameButton.isMouseOver(mouseX, mouseY))
                return this.editServerListNameButton.mouseClicked(mouseX, mouseY, button);
            if (this.scrollDropdown.isMouseOver(mouseX, mouseY)) {
                System.out.println("Does nothing yet!");
                return true;
            }
        }
        List<TabWidget> children = children();
        if (mouseY >= top && mouseY <= bottom && mouseX >= (needsScroll ? 20 : 0) && mouseX <= width - (needsScroll ? -60 : -40))
            for (TabWidget child : children) {
                int a = child.getActualWidth();
                if (mouseX >= w && mouseX < w + a) return child.mouseClicked(mouseX, mouseY, button);
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
        private final Text message;
        private final PressAction onPress;
        public boolean selected;

        public TabWidget(int width, CustomFileServerList serverList, PressAction onPress) {
            this.width = width;
            message = Text.of(serverList == null ? "Main" : serverList.getName());
            this.onPress = onPress;
        }

        @Override
        public Text getNarration() {
            return message;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            onPress.onPress(this);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
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
            int i = selected ? 0 : hovered ? 2 : 1;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            drawTexture(matrices, x, y, 0, i * 20, Math.min(entryWidth, w / 2), entryHeight);
            drawTexture(matrices, x + w / 2, y, 200 - w / 2, i * 20, Math.min(entryWidth - w / 2, w / 2), entryHeight);
            drawCenteredText(matrices, textRenderer, message, x + w / 2, y + (height - 8) / 2, 0xffffffff);
        }

        public int getActualWidth() {
            return width < 0 ? Math.max(20, client.textRenderer.getWidth(message) + 12) : width;
        }

        @Environment(EnvType.CLIENT)
        public interface PressAction {
            void onPress(TabWidget button);
        }
    }
}
