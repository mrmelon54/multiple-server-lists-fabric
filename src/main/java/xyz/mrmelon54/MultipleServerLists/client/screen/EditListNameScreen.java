package xyz.mrmelon54.MultipleServerLists.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

@Environment(EnvType.CLIENT)
public class EditListNameScreen extends Screen {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("multiple-server-lists", "textures/gui/edit_server_name.png");
    private final Screen parent;
    private final CustomFileServerList serverList;
    private static final int backgroundWidth = 176;
    private static final int backgroundHeight = 72;
    private int x;
    private int y;
    private TextFieldWidget nameField;
    private ButtonWidget renameButton;

    public EditListNameScreen(Text title, Screen parent, CustomFileServerList serverList) {
        super(title);
        this.parent = parent;
        this.serverList = serverList;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;

        if (this.serverList == null) {
            close();
            return;
        }

        this.renameButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("multiple-server-lists.screen.edit-list-name.button.rename"), (button) -> {
            String a = this.nameField.getText();
            if (isValidName(a)) {
                serverList.setName(a);
                serverList.saveFile();
                close();
            }
        }).dimensions(this.x + 7, this.y + 45, 50, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("multiple-server-lists.screen.edit-list-name.button.cancel"), (button) -> close()).dimensions(this.x + 119, this.y + 45, 50, 20).build());

        this.nameField = new TextFieldWidget(this.textRenderer, this.x + 62, this.y + 24, 103, 12, Text.translatable("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(100);
        this.nameField.setChangedListener(this::onRenamed);
        this.nameField.setText(this.serverList.getName());
        this.addSelectableChild(this.nameField);
        this.setInitialFocus(this.nameField);
        this.nameField.setEditable(true);
    }

    private boolean isValidName(String a) {
        return !a.trim().equals("");
    }

    @Override
    public void tick() {
        super.tick();
        this.nameField.tick();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }

    @Override
    public void removed() {
        super.removed();
        //if (this.client != null) this.client.keyboard.setRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.client != null && this.client.player != null)
            this.client.player.closeHandledScreen();

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.parent.render(context, -2000, -2000, delta);
        context.fillGradient(0, 0, this.width, this.height, 0xc0101010, 0xd0101010);

        context.drawTexture(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, backgroundWidth, backgroundHeight);
        context.drawTexture(BACKGROUND_TEXTURE, this.x + 59, this.y + 20, 0, backgroundHeight, 110, 16);

        if (this.nameField != null) this.nameField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    private void onRenamed(String value) {
        if (this.renameButton != null) this.renameButton.active = isValidName(value);
    }

    @Override
    public void close() {
        if (this.client != null && this.parent != null) this.client.setScreen(this.parent);
        else super.close();
    }
}
