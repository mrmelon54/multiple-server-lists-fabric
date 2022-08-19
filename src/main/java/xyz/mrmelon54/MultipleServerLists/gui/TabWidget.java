package xyz.mrmelon54.MultipleServerLists.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class TabWidget extends PressableWidget {
    private static final Identifier SERVER_TABS_TEXTURE = new Identifier("multiple-server-lists", "textures/gui/server-tabs.png");
    public static final TooltipSupplier EMPTY = (tab, matrices, mouseX, mouseY) -> {
    };

    private final PressAction onPress;
    private final TooltipSupplier tooltipSupplier;

    public TabWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        this(x, y, width, height, message, onPress, EMPTY);
    }

    public TabWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        this.tooltipSupplier.supply((text) -> builder.put(NarrationPart.HINT, text));
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("mouseClicked");
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        System.out.println("mouseReleased");
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int w = width;
        if (w < 0) w = Math.min(-width, textRenderer.getWidth(getMessage()) + 12);
        boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + height;
        int i = hovered ? 1 : 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, 0, i * 20, w / 2, height);
        drawTexture(matrices, x + w / 2, y, 200 - w / 2, i * 20, w / 2, height);
        int j = active ? 16777215 : 10526880;
        drawCenteredText(matrices, textRenderer, getMessage(), x + w / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
        if (isHovered()) renderTooltip(matrices, mouseX, mouseY);
    }

    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    @Environment(EnvType.CLIENT)
    public interface TooltipSupplier {
        void onTooltip(TabWidget button, MatrixStack matrices, int mouseX, int mouseY);

        default void supply(Consumer<Text> consumer) {
        }
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(TabWidget button);
    }
}
