package xyz.mrmelon54.MultipleServerLists.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.mrmelon54.MultipleServerLists.client.screen.EditListNameScreen;
import xyz.mrmelon54.MultipleServerLists.duck.EntryListWidgetDuckProvider;
import xyz.mrmelon54.MultipleServerLists.duck.MultiplayerScreenDuckProvider;
import xyz.mrmelon54.MultipleServerLists.util.CustomFileServerList;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen implements MultiplayerScreenDuckProvider {
    private int currentTab = 0;
    private ButtonWidget editServerListNameButton;
    private ItemStack featherStack;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    @Shadow
    private ServerList serverList;

    @Shadow
    @Final
    private Screen parent;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injectedInit(CallbackInfo ci) {
        featherStack = new ItemStack(Items.FEATHER);

        if (this.serverListWidget instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
            entryListWidgetDuckProvider.setRefreshCallback(this::reloadServerList);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), (button) -> {
            currentTab--;
            if (this.serverListWidget instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
                entryListWidgetDuckProvider.resetScrollPosition();
            reloadServerList();
        }).dimensions(0, 0, 20, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), (button) -> {
            currentTab++;
            if (this.serverListWidget instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
                entryListWidgetDuckProvider.resetScrollPosition();
            reloadServerList();
        }).dimensions(20, 0, 20, 20).build());
        this.editServerListNameButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(""), (button) -> {
            if (serverList instanceof CustomFileServerList customFileServerList) {
                EditListNameScreen editListNameScreen = new EditListNameScreen(Text.translatable("multiple-server-lists.screen.edit-list-name.title"), this, customFileServerList);
                if (this.client != null)
                    this.client.setScreen(editListNameScreen);
            }
        }).dimensions(40, 0, 20, 20).build());
        reloadServerList();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void injectedRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client != null) {
            if (featherStack != null)
                this.client.getItemRenderer().renderInGui(new MatrixStack(), featherStack, 42, 2);
            if (currentTab == 0) {
                this.client.textRenderer.draw(matrices, Text.literal("Main"), 64, 6, 0xffffff);
            } else {
                if (serverList instanceof CustomFileServerList customFileServerList)
                    this.client.textRenderer.draw(matrices, Text.literal(customFileServerList.getName()), 64, 6, 0xffffff);
                else
                    this.client.textRenderer.draw(matrices, Text.literal("Page " + currentTab), 64, 6, 0xffffff);
            }
        }
    }

    private void reloadServerList() {
        // Stop underflow
        if (currentTab < 0) currentTab = 0;

        serverList = this.getServerListForTab(currentTab);
        serverList.loadFile();
        this.serverListWidget.setServers(serverList);

        if (this.editServerListNameButton != null)
            this.editServerListNameButton.active = serverList instanceof CustomFileServerList;
    }

    @Override
    public ServerList getServerListForTab(int tab) {
        if (tab < 0) return null;
        return tab == 0 ? new ServerList(this.client) : new CustomFileServerList(this.client, tab);
    }

    @Override
    public int getCurrentTab() {
        return currentTab;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void refresh(CallbackInfo ci) {
        MultiplayerScreen multiplayerScreen = new MultiplayerScreen(this.parent);
        if (multiplayerScreen instanceof MultiplayerScreenDuckProvider duckProvider)
            duckProvider.setCurrentTab(currentTab);
        MinecraftClient.getInstance().setScreen(multiplayerScreen);
        ci.cancel();
    }

    @Override
    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }
}
